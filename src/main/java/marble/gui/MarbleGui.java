package marble.gui;

import imgui.*;
import imgui.flag.*;
import imgui.type.ImString;
import imgui.type.ImBoolean;

import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;

import marble.listeners.MouseListener;

public final class MarbleGui {

    public static int drawCalls = 0;
    public static int totalVertexCount = 0;
    public static final ImBoolean polygonMode = new ImBoolean(false);

    private static final ImBoolean vsync = new ImBoolean(true);

    private MarbleGui()
    {
        throw new UnsupportedOperationException();
    }

    public static void drawDiagnostics(float dt)
    {
        ImGui.begin("Diagnostics", ImGuiWindowFlags.NoCollapse);
        ImGui.text(String.format("%.1f fps", 1 / dt));
        ImGui.text(String.format("%.3f ms/frame", dt * 1000));

        ImGui.text(String.format("%d vertices", totalVertexCount));
        ImGui.text(String.format("%o Draw calls", drawCalls));
        drawCalls = 0;
        ImGui.text(String.format("Pos: %s, %s", MouseListener.getX(), MouseListener.getY()));
        ImGui.text(String.format("Delta: %s, %s", MouseListener.mouseDelta().x, MouseListener.mouseDelta().y));
        ImGui.text(String.format("Dragging: %b", MouseListener.isDragging()));

        if (ImGui.checkbox("Vsync", vsync)) {
            if(vsync.get())
                glfwSwapInterval(GLFW_TRUE);
            else
                glfwSwapInterval(GLFW_FALSE);
        }

        ImGui.checkbox("Polygon", polygonMode);
        ImGui.end();
    }

    public static void setupTheme()
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

    public static String inputText(String label, String text)
    {
        ImGui.pushID(label);
        ImString outString = new ImString(text, 256);
        if (ImGui.inputText("##" + label, outString)) {
            ImGui.popID();
            return outString.get();
        }
        ImGui.popID();
        return text;
    }

    public static void vec3Controller(String label, Vector3f values)
    {
        ImGui.pushID(label);
        ImGui.text(label);
        ImGui.nextColumn();

        float widthEach = ImGui.calcItemWidth() / 3.0f;

        ImGui.pushItemWidth(widthEach);
        float[] vecValuesX = {values.x};
        ImGui.dragFloat("##X", vecValuesX, 0.1f);
        ImGui.sameLine();
        ImGui.text("X");
        ImGui.sameLine();
        ImGui.popItemWidth();

        ImGui.pushItemWidth(widthEach);
        float[] vecValuesY = {values.y};
        ImGui.dragFloat("##Y", vecValuesY, 0.1f);
        ImGui.sameLine();
        ImGui.text("Y");
        ImGui.sameLine();
        ImGui.popItemWidth();

        ImGui.pushItemWidth(widthEach);
        float[] vecValuesZ = {values.z};
        ImGui.dragFloat("##Z", vecValuesZ, 0.1f);
        ImGui.sameLine();
        ImGui.text("Z");
        ImGui.popItemWidth();

        ImGui.popID();
        values.x = vecValuesX[0];
        values.y = vecValuesY[0];
        values.z = vecValuesZ[0];
    }

    public static void colorEdit4(String label, Vector4f color)
    {
        ImGui.text(label);
        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorEdit4(label, imColor))
            color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
    }

    public static float dragFloat(String label, float value)
    {
        ImGui.text(label);
        float[] valArr = {value};
        ImGui.dragFloat("##" + label, valArr, 0.1f);
        return valArr[0];
    }

    public static void text(String text)
    {
        ImGui.text(text);
    }

}
