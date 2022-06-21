package marble.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImGuiViewport;
import imgui.flag.ImGuiStyleVar;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiWindowFlags;

import marble.renderer.Framebuffer;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.GL_RENDERER;

import marble.Application;
import marble.gui.MarbleGui;
import marble.scene.Scene;
import marble.scene.emptyScene;
import marble.scene.SceneSerializer;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;

public class EditorLayer {

    public static Framebuffer GAME_FRAMEBUFFER;
    public static Framebuffer EDITOR_FRAMEBUFFER;

    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 editorViewportSize = new ImVec2();

    public static boolean sceneRunning = false;
    public static Scene currentScene, runtimeScene;

    private boolean inputFlag;
    private final PanelManager panelManager;
    private final SceneSerializer sceneSerializer;

    private final ImVec2[] editorViewportBounds = { new ImVec2(), new ImVec2() };

    public EditorLayer()
    {
        var gameFbSpec = new FramebufferSpecification();
        gameFbSpec.width = 1280;
        gameFbSpec.height = 720;

        var editorFbSpec = new FramebufferSpecification();
        editorFbSpec.width = 1280;
        editorFbSpec.height = 720;

        GAME_FRAMEBUFFER = Framebuffer.create(gameFbSpec);
        EDITOR_FRAMEBUFFER = Framebuffer.create(editorFbSpec);

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
    }

    public void onSceneUpdate(float dt)
    {
        drawEditorViewport(dt);
        MarbleGui.onImGuiRender(dt);
        currentScene.onSceneUpdate(dt);
    }

    public void onSceneRender()
    {
        currentScene.onSceneRender();
    }

    private void HandleWindowResize(ImVec2 viewportSize, Framebuffer framebuffer)
    {
        ImVec2 view = ImGui.getContentRegionAvail();
        if ( view.x != viewportSize.x || view.y != viewportSize.y )
        {
            // Window too small or collapsed
            if (view.x == 0 || view.y == 0)
                return;

            viewportSize.x = view.x;
            viewportSize.y = view.y;

            // RecreateFramebuffer
            framebuffer.getSpecification().width = (int) viewportSize.x;
            framebuffer.getSpecification().height = (int) viewportSize.y;
            framebuffer.recreate();
            ConsolePanel.log("Window resized!");
        }
    }

    private void drawEditorViewport(float dt)
    {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);

        ImVec2 viewportOffset = ImGui.getCursorPos();
        HandleWindowResize(editorViewportSize, EDITOR_FRAMEBUFFER);

        editorViewportSize = ImGui.getContentRegionAvail();

        setEditorViewportInputFlag();
        handleEditorViewportInput(dt);

        int textureId = EDITOR_FRAMEBUFFER.textureId;
        ImGui.image(textureId, editorViewportSize.x, editorViewportSize.y, 0, 1, 1, 0);

        Guizmo.onImGuiRender();

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
            float mx = mousePos.x;
            float my = mousePos.y;
            mx -= editorViewportBounds[0].x;
            my -= editorViewportBounds[0].y;

            var viewportSize = new ImVec2(editorViewportBounds[1].x - editorViewportBounds[0].x, editorViewportBounds[1].y - editorViewportBounds[0].y);
            my = viewportSize.y - my;

            int mouseX = (int) mx;
            int mouseY = (int) my;

            // Within viewport
            if (mouseX >= 0 && mouseY >= 0 && mouseX < (int) viewportSize.x && mouseY < (int) viewportSize.y) {
                if (ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_1))
                {
                    var selectedEntity = currentScene.getEntityFromUUID(EditorLayer.EDITOR_FRAMEBUFFER.readPixel(mouseX, mouseY));
                    if (selectedEntity != null)
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
        HandleWindowResize(gameViewportSize, GAME_FRAMEBUFFER);
        gameViewportSize = ImGui.getContentRegionAvail();
        int textureId = GAME_FRAMEBUFFER.textureId;
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
        currentScene.cleanUp();
        currentScene = loadedScene;
        currentScene.init();
        currentScene.start();
    }

    private ImVec2 getViewportSize()
    {
        ImVec2 viewportSize = ImGui.getWindowSize();
        float aspectRatio = viewportSize.x / viewportSize.y;
        float aspectWidth = viewportSize.x;
        float aspectHeight = aspectWidth / aspectRatio;
        if (aspectHeight > viewportSize.y)
        {
            aspectHeight = viewportSize.y;
            aspectWidth = aspectHeight * aspectRatio;
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getRenderingPos(ImVec2 viewportSize)
    {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        float viewportX = (windowSize.x / 2.0f) - (viewportSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (viewportSize.y / 2.0f);
        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    private void handleEditorViewportInput(float dt)
    {
        if (inputFlag)
        {
            var editorCam = currentScene.editorCamera;
            float camSpeed = 10 * dt;
            float camRotSpeed = 15 * dt;

            glfwSetInputMode(Application.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            editorCam.rotate(-MouseListener.mouseDelta().x * camRotSpeed, -MouseListener.mouseDelta().y * camRotSpeed, 0);

            if (KeyListener.isKeyPressed(GLFW_KEY_W))
                editorCam.move(0, 0, -camSpeed);
            if (KeyListener.isKeyPressed(GLFW_KEY_S))
                editorCam.move(0, 0, camSpeed);
            if (KeyListener.isKeyPressed(GLFW_KEY_A))
                editorCam.move(-camSpeed, 0, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_D))
                editorCam.move(camSpeed, 0, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_E))
                editorCam.move(0, -camSpeed, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_Q))
                editorCam.move(0, camSpeed, 0);
        }
        else
        {
            glfwSetInputMode(Application.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public void cleanUp()
    {
        currentScene.cleanUp();
    }

}
