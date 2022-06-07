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
import marble.scene.SceneSerializer;
import marble.scene.emptyScene;
import marble.scene.Scene;
import marble.imgui.MarbleGui;
import marble.renderer.FrameBuffer;

public class EditorLayer {

    public static boolean allowSceneViewportInput = false;
    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 editorViewportSize = new ImVec2();
    public static final FrameBuffer gameViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());
    public static final FrameBuffer editorViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());

    public static Scene currentScene, runtimeScene;

    private final ConsolePanel consolePanel;
    private final SceneSerializer sceneSerializer;
    private final SceneHierarchyPanel sceneHierarchy;
    private final ContentBrowserPanel contentBrowserPanel;

    public EditorLayer()
    {
        consolePanel = new ConsolePanel();
        sceneSerializer = new SceneSerializer();
        sceneHierarchy = new SceneHierarchyPanel();
        contentBrowserPanel = new ContentBrowserPanel();

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
        sceneHierarchy.onUpdate();
        contentBrowserPanel.onUpdate();

        drawGameViewport();
        drawEditorViewport();
        drawEntityInspector();

        MarbleGui.drawDiagnostics(dt);
        currentScene.onUpdate(dt);
        currentScene.onRender();
    }

    private void drawEditorViewport()
    {
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        setSceneViewportInputFlag();
        editorViewportSize = getViewportSize();
        ImVec2 windowPos = getRenderingPos(editorViewportSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);
        int textureId = editorViewportFramebuffer.getTextureId();
        ImGui.image(textureId, editorViewportSize.x, editorViewportSize.y, 0, 1, 1, 0);
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        // Drag and drop
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

    private void setSceneViewportInputFlag()
    {
        if (ImGui.isWindowHovered() && ImGui.isMouseClicked(GLFW_MOUSE_BUTTON_2))
            allowSceneViewportInput = true;
        if (ImGui.isMouseReleased(GLFW_MOUSE_BUTTON_2))
            allowSceneViewportInput = false;
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
            if (ImGui.menuItem("Load scene")) openScene("TODO");
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

    private void drawEntityInspector()
    {
        ImGui.begin("Inspector");
        if (sceneHierarchy.selectedEntity != null)
            sceneHierarchy.selectedEntity.setupInspector();
        ImGui.end();
    }

    public void cleanUp()
    {
        currentScene.cleanUp();
    }

}
