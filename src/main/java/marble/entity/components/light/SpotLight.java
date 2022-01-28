package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class SpotLight extends Light {

    @Override
    public void setupInspector()
    {
        // TODO: Make Light a single component with options
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Spot Light", nodeFlags);
        if (nodeOpen) {
            ImGui.treePop();
        }
    }
}
