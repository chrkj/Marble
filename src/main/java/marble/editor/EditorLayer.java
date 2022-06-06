package marble.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImGuiViewport;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiWindowFlags;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import marble.Application;
import marble.scene.SceneManager;
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

    private final ConsolePanel console;
    private final SceneManager sceneManager;
    private final SceneHierarchyPanel sceneHierarchy;
    private final ContentBrowserPanel contentBrowserPanel;

    public EditorLayer()
    {
        console = new ConsolePanel();
        sceneManager = new SceneManager();
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
        console.onUpdate();
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

        // Gizmos (TODO: Merge Framebuffer with gizmos)
        if (sceneHierarchy.selectedEntity != null)
        {
            ImGuizmo.setOrthographic(false);
            ImGuizmo.setEnabled(true);
            ImGuizmo.setDrawList();
            float windowWidth = ImGui.getWindowWidth();
            float windowHeight = ImGui.getWindowHeight();
            ImGuizmo.setRect(ImGui.getWindowPos().x, ImGui.getWindowPos().y, windowWidth, windowHeight);

            var camera = currentScene.getEditorCamera();
            var view = camera.getViewMatrix();
            var proj = camera.getProjectionMatrixEditor();
            var transform = sceneHierarchy.selectedEntity.transform;

            float[] v = {
                    view.m00(), view.m10(), view.m20(), view.m30(),
                    view.m01(), view.m11(), view.m21(), view.m31(),
                    view.m02(), view.m12(), view.m22(), view.m32(),
                    view.m03(), view.m13(), view.m23(), view.m33()};

            float[] p = {
                    proj.m00(), proj.m10(), proj.m20(), proj.m30(),
                    proj.m01(), proj.m11(), proj.m21(), proj.m31(),
                    proj.m02(), proj.m12(), proj.m22(), proj.m32(),
                    proj.m03(), proj.m13(), proj.m23(), proj.m33()};

            float[] t = new float[16];

            ImGuizmo.recomposeMatrixFromComponents(t,
                    new float[]{transform.position.x, transform.position.y, transform.position.z},
                    new float[]{transform.rotation.x, transform.rotation.y, transform.rotation.z},
                    new float[]{transform.scale.x, transform.scale.y, transform.scale.z});

            ImGuizmo.manipulate(v, p, t, Operation.TRANSLATE, Mode.LOCAL);
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
        if (ImGui.isWindowHovered() && ImGui.isMouseClicked(1))
            allowSceneViewportInput = true;
        if (ImGui.isMouseReleased(1))
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
            if (ImGui.menuItem("Save scene")) sceneManager.serialize(currentScene);
            if (ImGui.menuItem("Load scene"))
            {
                Scene loadedScene = sceneManager.deSerialize("TODO"); // TODO: Fix filepath acquirement
                if (loadedScene == null ) return;
                currentScene.cleanUp();
                currentScene = loadedScene;
                currentScene.init();
                currentScene.start();
            }
            if (ImGui.menuItem("Exit")) glfwSetWindowShouldClose(Application.windowPtr, true);
            ImGui.endMenu();
        }
        ImGui.endMenuBar();
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
