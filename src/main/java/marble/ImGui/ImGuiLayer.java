package marble.imgui;

import imgui.ImGui;
import imgui.type.ImBoolean;

import static org.lwjgl.glfw.GLFW.*;


public class ImGuiLayer {

    private final ImBoolean vsync = new ImBoolean(true);

    public void createLayer(float dt)
    {
        ImGui.begin("Diagnostics");
        ImGui.text(String.format("%.1f", 1 / dt) + " fps");
        ImGui.text(String.format("%.3f", dt * 1000) + " ms/frame");
        if (ImGui.checkbox("Vsync", vsync)) {
            if(vsync.get())
                glfwSwapInterval(GLFW_TRUE);
            else
                glfwSwapInterval(GLFW_FALSE);
        }
        ImGui.end();
    }
}
