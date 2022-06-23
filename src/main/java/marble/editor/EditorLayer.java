package marble.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiWindowFlags;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.GL_RENDERER;

import marble.Application;
import marble.gui.MarbleGui;
import marble.scene.Scene;
import marble.scene.emptyScene;
import marble.scene.SceneSerializer;
import marble.renderer.Framebuffer;

public class EditorLayer {

    public static Framebuffer gameViewportFb;
    public static Framebuffer editorViewportFb;

    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 editorViewportSize = new ImVec2();

    public static boolean sceneRunning = false;
    public static Scene currentScene, runtimeScene;

    public static boolean inputFlag;
    private final PanelManager panelManager;
    private final SceneSerializer sceneSerializer;

    private final ImVec2[] editorViewportBounds = { new ImVec2(), new ImVec2() };

    public EditorLayer()
    {
        var gameFbSpec = new Framebuffer.FramebufferSpecification(Framebuffer.TextureFormat.RGB8, Framebuffer.TextureFormat.DEPTH24_STENCIL8);
        gameFbSpec.width = 1280;
        gameFbSpec.height = 720;
        gameViewportFb = Framebuffer.create(gameFbSpec);

        var editorFbSpec = new Framebuffer.FramebufferSpecification(Framebuffer.TextureFormat.RGB8, Framebuffer.TextureFormat.RED_INTEGER, Framebuffer.TextureFormat.DEPTH24_STENCIL8);
        editorFbSpec.width = 1280;
        editorFbSpec.height = 720;
        editorViewportFb = Framebuffer.create(editorFbSpec);

        sceneSerializer = new SceneSerializer();

        panelManager = new PanelManager();
        panelManager.addPanel(new ToolPanel());
        panelManager.addPanel(new ConsolePanel());
        panelManager.addPanel(new FileDialogPanel());
        panelManager.addPanel(new SceneHierarchyPanel());
        panelManager.addPanel(new ContentBrowserPanel());
        panelManager.addPanel(new EntityInspectorPanel());

        currentScene = new emptyScene("Empty Scene");
        currentScene.init();
        currentScene.start();

        ConsolePanel.log("LWJGL Version: " + Version.getVersion() + "!");
        ConsolePanel.log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        ConsolePanel.log("Renderer: " + GL30.glGetString(GL_RENDERER));
        ConsolePanel.log("Version: " + GL30.glGetString(GL_VERSION));
    }

    public void onImGuiRender()
    {
        setupDockspace();
        panelManager.onImGuiRender();
        drawGameViewport();
        drawEditorViewport();
    }

    public void onSceneUpdate(float dt)
    {
        MarbleGui.onImGuiRender(dt); // TODO: Move this to onImGuiRender
        currentScene.onSceneUpdate(dt);
    }

    public void onSceneRender()
    {
        currentScene.onSceneRender();
    }

    public void cleanUp()
    {
        currentScene.cleanUp();
    }

    private void handleWindowResize(ImVec2 viewportSize, Framebuffer framebuffer)
    {
        ImVec2 view = ImGui.getContentRegionAvail();
        if ( view.x != viewportSize.x || view.y != viewportSize.y )
        {
            // Window too small or collapsed
            if (view.x == 0 || view.y == 0)
                return;

            viewportSize.x = view.x;
            viewportSize.y = view.y;

            // Recreate framebuffer
            framebuffer.getSpecification().width = (int) viewportSize.x;
            framebuffer.getSpecification().height = (int) viewportSize.y;
            framebuffer.recreate();
        }
    }

    private void drawEditorViewport()
    {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);

        ImVec2 viewportOffset = ImGui.getCursorPos();
        handleWindowResize(editorViewportSize, editorViewportFb);

        editorViewportSize = ImGui.getContentRegionAvail();

        setEditorViewportInputFlag();

        int textureId = editorViewportFb.getColorAttachmentRendererID();
        ImGui.image(textureId, editorViewportSize.x, editorViewportSize.y, 0, 1, 1, 0);
        Gizmo.onImGuiRender();

        // DragDrop open scene
        if (ImGui.beginDragDropTarget())
        {
            var payload = ImGui.acceptDragDropPayload("CONTENT_BROWSER_FILE");
            if (payload != null)
                openScene(payload.toString());
        }

        // Mouse picking
        {
            ImVec2 windowSize = ImGui.getWindowSize();
            ImVec2 minBound = ImGui.getWindowPos();
            minBound.x += viewportOffset.x;
            minBound.y += viewportOffset.y - 19; // TODO: FIX THIS (hardcoded tab offset)

            ImVec2 maxBound = new ImVec2(minBound.x + windowSize.x, minBound.y + windowSize.y);
            editorViewportBounds[0] = new ImVec2(minBound.x, minBound.y);
            editorViewportBounds[1] = new ImVec2(maxBound.x, maxBound.y);

            var mousePos = ImGui.getMousePos();
            float x = mousePos.x;
            float y = mousePos.y;
            x -= editorViewportBounds[0].x;
            y -= editorViewportBounds[0].y;

            var viewportSize = new ImVec2(editorViewportBounds[1].x - editorViewportBounds[0].x, editorViewportBounds[1].y - editorViewportBounds[0].y);
            y = viewportSize.y - y;

            int mouseX = (int) x;
            int mouseY = (int) y;

            // Within viewport
            if (mouseX >= 0 && mouseY >= 0 && mouseX < (int) viewportSize.x && mouseY < (int) viewportSize.y)
            {
                if (ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_1) && !Gizmo.inUse())
                {
                    var selectedEntity = currentScene.getEntityFromUUID(EditorLayer.editorViewportFb.readPixel(mouseX, mouseY, 1));
                    SceneHierarchyPanel.setSelectedEntity(selectedEntity);
                }
            }
        }

        ImGui.end();
        ImGui.popStyleVar(1);
    }

    private void drawGameViewport()
    {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        handleWindowResize(gameViewportSize, gameViewportFb);
        gameViewportSize = ImGui.getContentRegionAvail();
        int textureId = gameViewportFb.getColorAttachmentRendererID();
        ImGui.image(textureId, gameViewportSize.x, gameViewportSize.y, 0, 1, 1, 0);
        ImGui.end();
        ImGui.popStyleVar(1);
    }

    private void setEditorViewportInputFlag()
    {
        if (ImGui.isWindowHovered() && ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_2))
        {
            inputFlag = true;
            return;
        }
        if (ImGui.isMouseReleased(GLFW_MOUSE_BUTTON_2))
            inputFlag = false;
    }

    private void setupDockspace()
    {
        int windowFlags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar;
        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());
        ImGui.setNextWindowPos(0.0f, 0.0f);
        ImGui.setNextWindowSize(Application.getWidth(), Application.getHeight());
        windowFlags |= ImGuiWindowFlags.NoCollapse |  ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);
        ImGui.dockSpace(ImGui.getID("Dockspace"));
        createMenuBar();
        ImGui.end();
    }

    private void createMenuBar()
    {
        var fileDialogPanel = (FileDialogPanel) panelManager.getPanel(FileDialogPanel.class);
        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File"))
        {
            // TODO: Shortcuts not handled
            if (ImGui.menuItem("Save scene", "ctrl-s")) sceneSerializer.serialize(currentScene);
            if (ImGui.menuItem("Open scene", "ctrl-o")) fileDialogPanel.open();
            if (ImGui.menuItem("Exit"))                             glfwSetWindowShouldClose(Application.windowPtr, true);
            ImGui.endMenu();
        }

        if (fileDialogPanel.isFileSelected())
            openScene(fileDialogPanel.getSelectedFilePath());

        ImGui.endMenuBar();
    }

    private void openScene(String filePath)
    {
        Scene loadedScene = sceneSerializer.deSerialize(filePath);
        if (loadedScene == null)
            return;
        SceneHierarchyPanel.setSelectedEntity(null);
        currentScene.cleanUp();
        currentScene = loadedScene;
        currentScene.init();
        currentScene.start();
    }

}
