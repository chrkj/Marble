package marble.ImGui;

import imgui.ImGui;
import Sandbox.EditorScene;

public class ImGuiLayer {
    public void createLayer()
    {
        ImGui.begin("Editor");
        ImGui.sliderFloat("Rotation speed", EditorScene.angelAmount, 0f, 2f);
        ImGui.end();
    }
}
