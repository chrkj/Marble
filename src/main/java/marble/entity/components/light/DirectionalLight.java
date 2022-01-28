package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.Vector4f;

public class DirectionalLight extends Light {

    public DirectionalLight()
    {
        this.intensity = 1f;
        this.color = new Vector4f(1,1,1,1);
    }


    @Override
    public void setupInspector()
    {
        // TODO: Make Light a single component with options
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Directional Light", nodeFlags);
        if (nodeOpen) {
            ImGui.treePop();
        }
    }
}
