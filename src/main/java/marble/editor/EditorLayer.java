package marble.editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImGuiViewport;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.imgui.MarbleConsole;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import marble.Application;
import marble.scene.SceneManager;
import marble.scene.emptyScene;
import marble.scene.Scene;
import marble.entity.Entity;
import marble.imgui.MarbleGui;
import marble.renderer.FrameBuffer;

public class EditorLayer {

    public static boolean allowSceneViewportInput = false;
    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 sceneViewportSize = new ImVec2();
    public static final FrameBuffer gameViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());
    public static final FrameBuffer sceneViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());

    private Entity selectedEntity;
    private Scene currentScene, runtimeScene;
    private final SceneManager sceneManager;

    public EditorLayer()
    {
        sceneManager = new SceneManager();
        currentScene = new emptyScene("Empty Scene");
        currentScene.init();
        currentScene.start();
        MarbleConsole.log("LWJGL Version: " + Version.getVersion() + "!");
        MarbleConsole.log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        MarbleConsole.log("Renderer: " + GL30.glGetString(GL_RENDERER));
        MarbleConsole.log("Version: " + GL30.glGetString(GL_VERSION));
    }

    public void onUpdate(float dt)
    {
        drawDockspace();
        MarbleConsole.draw();
        drawSceneHierarchy();
        drawEntityInspector();
        drawSceneViewport();
        drawGameViewport();
        ImGui.showDemoWindow();
        MarbleGui.drawDiagnostics(dt);
        currentScene.onUpdate(dt);
        currentScene.onRender();
    }

    private void drawSceneViewport()
    {
        ImGui.begin("Scene", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        setSceneViewportInputFlag();
        sceneViewportSize = getViewportSize();
        ImVec2 windowPos = getRenderingPos(sceneViewportSize);
        ImGui.setCursorPos(windowPos.x, windowPos.y);
        int textureId = sceneViewportFramebuffer.getTextureId();
        ImGui.image(textureId, sceneViewportSize.x, sceneViewportSize.y, 0, 1, 1, 0);
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

    private void drawDockspace()
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

    private void drawSceneHierarchy()
    {
        ImGui.begin("Hierarchy");
        int nodeFlags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        for (Entity entity : currentScene.getEntities())
            recursiveDrawCall(entity, nodeFlags);
        ImGui.end();
    }

    private void recursiveDrawCall(Entity entity, int nodeFlags)
    {
        int currentFlags = nodeFlags;
        if (entity.getChildren().size() == 0) currentFlags |= ImGuiTreeNodeFlags.Leaf;
        if (entity == selectedEntity)         currentFlags |= ImGuiTreeNodeFlags.Selected;

        boolean nodeOpen;
        if (entity.name.length() == 0)
            nodeOpen = ImGui.treeNodeEx("##" + entity.name, currentFlags);
        else
            nodeOpen = ImGui.treeNodeEx(entity.name, currentFlags);

        if (ImGui.isItemClicked())
            selectedEntity = entity;

        if (nodeOpen) {
            for (Entity child : entity.getChildren())
                recursiveDrawCall(child, nodeFlags);
            ImGui.treePop();
        }

        // TODO: Handle unselecting when clicking away from the entity
    }

    private void drawEntityInspector()
    {
        ImGui.begin("Inspector");
        if (selectedEntity != null)
            selectedEntity.setupInspector();
        ImGui.end();
    }

    public void cleanUp()
    {
        currentScene.cleanUp();
    }

}
