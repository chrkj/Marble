package marble.imgui;

import imgui.ImGui;
import imgui.type.ImBoolean;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    public static int drawCalls = 0;
    private static final ImBoolean vsync = new ImBoolean(true);
    public static final ImBoolean polygonMode = new ImBoolean(false);
    private static final ImBoolean demoWindow = new ImBoolean(false);

    public static void updateDiagnosticLayer(float dt)
    {
        ImGui.begin("Diagnostics");
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
        ImGui.sameLine();
        ImGui.checkbox("Demo Window", demoWindow);
        if (demoWindow.get())
            ImGui.showDemoWindow();
        ImGui.checkbox("Polygon", polygonMode);
        ImGui.end();
    }

}
