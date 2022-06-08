package marble.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImGuiViewport;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiWindowFlags;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import marble.Application;
import marble.scene.Scene;
import marble.scene.emptyScene;
import marble.scene.SceneSerializer;
import marble.gui.MarbleGui;
import marble.renderer.FrameBuffer;

public class EditorLayer {

    public static boolean allowEditorViewportInput = false;
    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 editorViewportSize = new ImVec2();
    public static final FrameBuffer gameViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());
    public static final FrameBuffer editorViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());

    public static Scene currentScene, runtimeScene;

    private final ConsolePanel consolePanel;
    private final SceneSerializer sceneSerializer;
    private final SceneHierarchyPanel sceneHierarchyPanel;
    private final ContentBrowserPanel contentBrowserPanel;
    private final EntityInspectorPanel entityInspectorPanel;

    public EditorLayer()
    {
        consolePanel = new ConsolePanel();
        sceneSerializer = new SceneSerializer();
        sceneHierarchyPanel = new SceneHierarchyPanel();
        contentBrowserPanel = new ContentBrowserPanel();
        entityInspectorPanel = new EntityInspectorPanel();

        currentScene = new emptyScene("Empty Scene");
        currentScene.init();
        currentScene.start();

        ConsolePanel.log("LWJGL Version: " + Version.getVersion() + "!");
        ConsolePanel.log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        ConsolePanel.log("Renderer: " + GL30.glGetString(GL_RENDERER));
        ConsolePanel.log("Version: " + GL30.glGetString(GL_VERSION));
    }

    public void onUpdate(float dt)
    {
        setupDockspace();
        consolePanel.onUpdate();
        sceneHierarchyPanel.onUpdate();
        contentBrowserPanel.onUpdate();
        entityInspectorPanel.onUpdate(sceneHierarchyPanel.selectedEntity);

        drawGameViewport();
        drawEditorViewport();

        MarbleGui.drawDiagnostics(dt);

        currentScene.onUpdate(dt);
        currentScene.onRender();
    }

    private void drawEditorViewport()
    {
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        setEditorViewportInputFlag();
        editorViewportSize = getViewportSize();
        ImVec2 windowPos = getRenderingPos(editorViewportSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);
        int textureId = editorViewportFramebuffer.getTextureId();
        ImGui.image(textureId, editorViewportSize.x, editorViewportSize.y, 0, 1, 1, 0);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        // DragDrop open scene
        if (ImGui.beginDragDropTarget())
        {
            var payload = ImGui.acceptDragDropPayload("CONTENT_BROWSER_FILE");
            if (payload != null)
                openScene(payload.toString());
        }
        ImGui.end();
    }

    private void drawGameViewport()
    {
        ImGui.begin("Game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        gameViewportSize = getViewportSize();
        ImVec2 windowPos = getRenderingPos(gameViewportSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);
        int textureId = gameViewportFramebuffer.getTextureId();
        ImGui.image(textureId, gameViewportSize.x, gameViewportSize.y, 0, 1, 1, 0);
        ImGui.end();
    }

    private void setEditorViewportInputFlag()
    {
        if (ImGui.isWindowHovered() && ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_2))
            allowEditorViewportInput = true;
        if (ImGui.isMouseReleased(GLFW_MOUSE_BUTTON_2))
            allowEditorViewportInput = false;
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
        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save scene")) sceneSerializer.serialize(currentScene);
            if (ImGui.menuItem("Open scene")) openScene("TODO");
            if (ImGui.menuItem("Exit")) glfwSetWindowShouldClose(Application.windowPtr, true);
            ImGui.endMenu();
        }
        ImGui.endMenuBar();
    }

    private void openScene(String filePath)
    {
        Scene loadedScene = sceneSerializer.deSerialize(filePath); // TODO: Fix filepath acquirement
        if (loadedScene == null ) return;
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
        if (aspectHeight > viewportSize.y) {
            aspectHeight = viewportSize.y;
            aspectWidth = aspectHeight * aspectRatio;
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getRenderingPos(ImVec2 viewportSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        float viewportX = (windowSize.x / 2.0f) - (viewportSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (viewportSize.y / 2.0f);
        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    public void cleanUp()
    {
        currentScene.cleanUp();
    }

}
