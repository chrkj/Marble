package marble.imgui;

import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;
import marble.Window;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    public static int drawCalls = 0;
    private static final ImBoolean vsync = new ImBoolean(true);
    public static final ImBoolean polygonMode = new ImBoolean(false);
    private static final ImBoolean demoWindow = new ImBoolean(false);
    public static ImVec2 gameViewportSize = new ImVec2();
    private static int stylesToPop = 0;
    public static ImVec2 getCursorScreenPos;
    public static ImVec2 getContentRegionAvail;

    public static void update(float dt)
    {
        pushImGuiStyles();
        setupDockspace();
        setupDiagnostics(dt);
        setupGameViewport();
        popImGuiStyles();
    }

    private static void setupDiagnostics(float dt)
    {
        ImGui.begin("Diagnostics", ImGuiWindowFlags.NoCollapse);
        ImGui.text(String.format("%.1f", 1 / dt) + " Fps");
        ImGui.text(String.format("%.3f", dt * 1000) + " ms/frame");
        ImGui.text(String.format("%o", drawCalls) + " Draw calls");

        drawCalls = 0;
        if (ImGui.checkbox("Vsync", vsync)) {
            if(vsync.get())
                glfwSwapInterval(GLFW_TRUE);
            else
                glfwSwapInterval(GLFW_FALSE);
        }
        ImGui.checkbox("Demo Window", demoWindow);
        if (demoWindow.get())
            ImGui.showDemoWindow();
        ImGui.checkbox("Polygon", polygonMode);
        ImGui.end();
    }

    private static void setupGameViewport()
    {
        ImGui.begin("Game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        getCursorScreenPos = ImGui.getCursorScreenPos();
        getContentRegionAvail = ImGui.getContentRegionAvail();

        gameViewportSize = getGameViewportSize();
        ImVec2 windowPos = getGameRenderingPos();
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.image(textureId, gameViewportSize.x, gameViewportSize.y, 0, 1, 1, 0);

        ImGui.end();
    }

    private static void setupDockspace() {
        int windowFlags = ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.MenuBar;

        ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getWorkPosX(), mainViewport.getWorkPosY());
        ImGui.setNextWindowSize(mainViewport.getWorkSizeX(), mainViewport.getWorkSizeY());
        ImGui.setNextWindowViewport(mainViewport.getID());
        ImGui.setNextWindowPos(0.0f, 0.0f);
        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());

        windowFlags |= ImGuiWindowFlags.NoCollapse |  ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;

        ImGui.begin("Dockspace", new ImBoolean(true), windowFlags);

        ImGui.dockSpace(ImGui.getID("Dockspace"));
        createMenubar();
        ImGui.end();
    }

    private static void createMenubar()
    {
        ImGui.beginMenuBar();
        if (ImGui.beginMenu("File")) {
            if (ImGui.menuItem("Save", "Ctrl+S")) {
            }
            if (ImGui.menuItem("Load", "Ctrl+O")) {
            }
            ImGui.endMenu();
        }
        ImGui.endMenuBar();

    }

    private static ImVec2 getGameViewportSize()
    {
        gameViewportSize = ImGui.getWindowSize();
        float aspectRatio = gameViewportSize.x / gameViewportSize.y;
        float aspectWidth = gameViewportSize.x;
        float aspectHeight = aspectWidth / aspectRatio;
        if (aspectHeight > gameViewportSize.y) {
            aspectHeight = gameViewportSize.y;
            aspectWidth = aspectHeight * aspectRatio;
        }
        return new ImVec2(aspectWidth, aspectHeight);
    }

    private static ImVec2 getGameRenderingPos() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        float viewportX = (windowSize.x / 2.0f) - (gameViewportSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (gameViewportSize.y / 2.0f);
        return new ImVec2(viewportX + ImGui.getCursorPosX(), viewportY + ImGui.getCursorPosY());
    }

    private static void pushImGuiStyles()
    {
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        stylesToPop++;
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0.0f, 0.0f);
        stylesToPop++;
        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        stylesToPop++;
    }

    private static void popImGuiStyles()
    {
        ImGui.popStyleVar(stylesToPop);
        stylesToPop = 0;
    }

}
