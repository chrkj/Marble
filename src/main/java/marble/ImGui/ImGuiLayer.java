package marble.imgui;

import imgui.*;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;
import imgui.type.ImBoolean;

import static org.lwjgl.glfw.GLFW.*;

import marble.Window;
import marble.listeners.MouseListener;

public class ImGuiLayer {

    public static int drawCalls = 0;
    public static int totalVertexCount = 0;
    public static ImVec2 gameViewportSize = new ImVec2();
    public static ImVec2 getCursorScreenPos = new ImVec2();
    public static final ImBoolean polygonMode = new ImBoolean(false);

    private static final ImBoolean vsync = new ImBoolean(true);
    private static final ImBoolean scrollToBottom = new ImBoolean(false);

    public static void update(float dt)
    {
        setupTheme();
        setupDockspace();
        setupDiagnostics(dt);
        setupGameViewport();
        ImGui.showDemoWindow();
        setupConsole();
    }

    private static void setupDiagnostics(float dt)
    {
        ImGui.begin("Diagnostics", ImGuiWindowFlags.NoCollapse);
        ImGui.text(String.format("%.1f fps", 1 / dt));
        ImGui.text(String.format("%.3f ms/frame", dt * 1000));
        ImGui.text(String.format("%d vertices", totalVertexCount));
        ImGui.text(String.format("%o Draw calls", drawCalls));
        ImGui.text(String.format("Pos: %s, %s", MouseListener.getX(), MouseListener.getY()));
        ImGui.text(String.format("Delta: %s, %s", MouseListener.mouseDelta().x, MouseListener.mouseDelta().y));
        ImGui.text(String.format("Dragging: %b", MouseListener.isDragging()));
        drawCalls = 0;

        if (ImGui.checkbox("Vsync", vsync)) {
            if(vsync.get())
                glfwSwapInterval(GLFW_TRUE);
            else
                glfwSwapInterval(GLFW_FALSE);
        }
        ImGui.checkbox("Polygon", polygonMode);
        ImGui.end();
    }

    private static void setupGameViewport()
    {
        ImGui.begin("Game", ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse | ImGuiWindowFlags.NoCollapse);
        getCursorScreenPos = ImGui.getCursorScreenPos();

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
            if (ImGui.menuItem("Exit"))
                glfwSetWindowShouldClose(Window.windowPtr, true);
            ImGui.endMenu();
        }
        ImGui.endMenuBar();
    }

    public static void setupConsole()
    {
        ImGui.setNextWindowSize(500, 400);
        ImGui.begin("Console");
        if (ImGui.button("Clear"))
            Logger.clear();
        ImGui.separator();
        ImGui.beginChild("scrolling");
        ImGui.textUnformatted(Logger.getBuffer().toString());
        if (scrollToBottom.get())
            ImGui.setScrollHereY(1f);
        scrollToBottom.set(false);
        ImGui.endChild();
        ImGui.end();
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

    private static void setupTheme()
    {
        ImGuiStyle style = ImGui.getStyle();

        style.setColor(ImGuiCol.Text,                    1.000f, 1.000f, 1.000f, 1.000f);
        style.setColor(ImGuiCol.TextDisabled,            0.500f, 0.500f, 0.500f, 1.000f);
        style.setColor(ImGuiCol.WindowBg,                0.180f, 0.180f, 0.180f, 1.000f);
        style.setColor(ImGuiCol.ChildBg,                 0.280f, 0.280f, 0.280f, 0.000f);
        style.setColor(ImGuiCol.PopupBg,                 0.313f, 0.313f, 0.313f, 1.000f);
        style.setColor(ImGuiCol.Border,                  0.266f, 0.266f, 0.266f, 1.000f);
        style.setColor(ImGuiCol.BorderShadow,            0.000f, 0.000f, 0.000f, 0.000f);
        style.setColor(ImGuiCol.FrameBg,                 0.160f, 0.160f, 0.160f, 1.000f);
        style.setColor(ImGuiCol.FrameBgHovered,          0.200f, 0.200f, 0.200f, 1.000f);
        style.setColor(ImGuiCol.FrameBgActive,           0.280f, 0.280f, 0.280f, 1.000f);
        style.setColor(ImGuiCol.TitleBg,                 0.148f, 0.148f, 0.148f, 1.000f);
        style.setColor(ImGuiCol.TitleBgActive,           0.148f, 0.148f, 0.148f, 1.000f);
        style.setColor(ImGuiCol.TitleBgCollapsed,        0.148f, 0.148f, 0.148f, 1.000f);
        style.setColor(ImGuiCol.MenuBarBg,               0.195f, 0.195f, 0.195f, 1.000f);
        style.setColor(ImGuiCol.ScrollbarBg,             0.160f, 0.160f, 0.160f, 1.000f);
        style.setColor(ImGuiCol.ScrollbarGrab,           0.277f, 0.277f, 0.277f, 1.000f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered,    0.300f, 0.300f, 0.300f, 1.000f);
        style.setColor(ImGuiCol.ScrollbarGrabActive,     1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.CheckMark,               1.000f, 1.000f, 1.000f, 1.000f);
        style.setColor(ImGuiCol.SliderGrab,              0.391f, 0.391f, 0.391f, 1.000f);
        style.setColor(ImGuiCol.SliderGrabActive,        1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.Button,                  1.000f, 1.000f, 1.000f, 0.000f);
        style.setColor(ImGuiCol.ButtonHovered,           1.000f, 1.000f, 1.000f, 0.156f);
        style.setColor(ImGuiCol.ButtonActive,            1.000f, 1.000f, 1.000f, 0.391f);
        style.setColor(ImGuiCol.Header,                  0.313f, 0.313f, 0.313f, 1.000f);
        style.setColor(ImGuiCol.HeaderHovered,           0.469f, 0.469f, 0.469f, 1.000f);
        style.setColor(ImGuiCol.HeaderActive,            0.469f, 0.469f, 0.469f, 1.000f);
        style.setColor(ImGuiCol.Separator,               0.266f, 0.266f, 0.266f, 1.000f);
        style.setColor(ImGuiCol.SeparatorHovered,        0.391f, 0.391f, 0.391f, 1.000f);
        style.setColor(ImGuiCol.SeparatorActive,         1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.ResizeGrip,              1.000f, 1.000f, 1.000f, 0.250f);
        style.setColor(ImGuiCol.ResizeGripHovered,       1.000f, 1.000f, 1.000f, 0.670f);
        style.setColor(ImGuiCol.ResizeGripActive,        1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.Tab,                     0.098f, 0.098f, 0.098f, 1.000f);
        style.setColor(ImGuiCol.TabHovered,              0.352f, 0.352f, 0.352f, 1.000f);
        style.setColor(ImGuiCol.TabActive,               0.195f, 0.195f, 0.195f, 1.000f);
        style.setColor(ImGuiCol.TabUnfocused,            0.098f, 0.098f, 0.098f, 1.000f);
        style.setColor(ImGuiCol.TabUnfocusedActive,      0.195f, 0.195f, 0.195f, 1.000f);
        style.setColor(ImGuiCol.DockingPreview,          1.000f, 0.391f, 0.000f, 0.781f);
        style.setColor(ImGuiCol.DockingEmptyBg,          0.180f, 0.180f, 0.180f, 1.000f);
        style.setColor(ImGuiCol.PlotLines,               0.469f, 0.469f, 0.469f, 1.000f);
        style.setColor(ImGuiCol.PlotLinesHovered,        1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.PlotHistogram,           0.586f, 0.586f, 0.586f, 1.000f);
        style.setColor(ImGuiCol.PlotHistogramHovered,    1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.TextSelectedBg,          1.000f, 1.000f, 1.000f, 0.156f);
        style.setColor(ImGuiCol.DragDropTarget,          1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.NavHighlight,            1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.NavWindowingHighlight,   1.000f, 0.391f, 0.000f, 1.000f);
        style.setColor(ImGuiCol.NavWindowingDimBg,       0.000f, 0.000f, 0.000f, 0.586f);
        style.setColor(ImGuiCol.ModalWindowDimBg,        0.000f, 0.000f, 0.000f, 0.586f);

        style.setChildRounding(4.0f);
        style.setFrameBorderSize(1.0f);
        style.setFrameRounding(2.0f);
        style.setGrabMinSize(7.0f);
        style.setPopupRounding(2.0f);
        style.setScrollbarRounding(12.0f);
        style.setScrollbarSize(13.0f);
        style.setTabBorderSize(1.0f);
        style.setTabRounding(0.0f);
        style.setWindowRounding(4.0f);
    }

}
