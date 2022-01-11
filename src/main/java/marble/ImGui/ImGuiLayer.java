package marble.ImGui;

import imgui.ImGui;
import Sandbox.EditorScene;

public class ImGuiLayer {
    public void createLayer()
    {
        ImGui.begin("Editor");
        ImGui.sliderFloat("Rotation speed", EditorScene.rotationSpeed, 0f, 5f);
        ImGui.sliderFloat("x", EditorScene.x, -10f, 10f);
        ImGui.sliderFloat("y", EditorScene.y, -10f, 10f);
        ImGui.sliderFloat("z", EditorScene.z, -10f, 10f);
        ImGui.end();
    }
}
