package marble;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;

import game.emptyScene;
import marble.scene.Scene;
import marble.entity.Entity;
import marble.imgui.Console;
import marble.imgui.ImGuiLayer;
import marble.renderer.FrameBuffer;

public class EditorLayer {

    public static Entity selectedEntity;
    public static Scene currentScene, runtimeScene;
    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 sceneViewportSize = new ImVec2();
    public static boolean allowSceneViewportInput = false;
    public static final Console console = new Console();
    public static final FrameBuffer gameViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());
    public static final FrameBuffer sceneViewportFramebuffer = new FrameBuffer(Application.getWidth(), Application.getHeight());


    public EditorLayer()
    {
        currentScene = new emptyScene();
        currentScene.init();
        currentScene.start();
        ImGuiLayer.setupTheme();
        Console.log("LWJGL Version: " + Version.getVersion() + "!");
        Console.log("Vendor: " + GL30.glGetString(GL30.GL_VENDOR));
        Console.log("Renderer: " + GL30.glGetString(GL_RENDERER));
        Console.log("Version: " + GL30.glGetString(GL_VERSION));
    }


    public void onUpdate(float dt)
    {
        drawDockspace();
        console.draw();
        drawSceneHierarchy();
        drawEntityInspector();
        drawSceneViewport();
        drawGameViewport();
        ImGuiLayer.drawDiagnostics(dt);
        currentScene.updateScene(dt);
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
        // Setup input handling when testing the game
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
            if (ImGui.menuItem("Exit"))
                glfwSetWindowShouldClose(Application.windowPtr, true);
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
        for (Entity entity : currentScene.getSceneEntities()) {
            int nodeFlags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
            if (entity.getChildren().size() == 0)
                nodeFlags |= ImGuiTreeNodeFlags.Leaf;
            if (entity == selectedEntity)
                nodeFlags |= ImGuiTreeNodeFlags.Selected;
            boolean nodeOpen;
            if (entity.name.length() == 0)
                nodeOpen = ImGui.treeNodeEx("##" + entity.name, nodeFlags);
            else
                nodeOpen = ImGui.treeNodeEx(entity.name, nodeFlags);
            if (nodeOpen) {
                // TODO: Add entity children
                ImGui.treePop();
            }
            // TODO: Handle unselecting when clicking away from the entity
            if (ImGui.isItemClicked())
                selectedEntity = entity;
        }
        ImGui.end();
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
