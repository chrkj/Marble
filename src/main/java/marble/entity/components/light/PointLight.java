package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import marble.gui.MarbleGui;

public class PointLight extends Light {

    public float linear = 0;
    public float constant = 0;
    public float exponent = 0;

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Point Light", nodeFlags);
        if (nodeOpen) {
            MarbleGui.colorEdit4("Color", color);
            intensity = MarbleGui.dragFloat("Intensity", intensity);
            linear = MarbleGui.dragFloat("Linear", linear);
            constant = MarbleGui.dragFloat("Constant", constant);
            exponent = MarbleGui.dragFloat("Exponent", exponent);
            ImGui.treePop();
        }
    }

    @Override
    public void renderGizmo()
    {

    }
}
