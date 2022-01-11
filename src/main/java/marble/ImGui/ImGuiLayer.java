package Marble.ImGui;

import imgui.ImGui;

public class ImGuiLayer {
    public void createLayer(float dt)
    {
        ImGui.begin("Editor");
        ImGui.text(String.format("%.3f", dt * 1000) + " ms/frame");
        ImGui.text(String.format("%.1f", 1 / dt) + " fps");
        ImGui.end();
    }
}
