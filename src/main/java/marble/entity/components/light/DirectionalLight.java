package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import org.joml.Vector4f;

import marble.gui.MarbleGui;

public class DirectionalLight extends Light {

    public DirectionalLight()
    {
        this.intensity = 1f;
        this.color = new Vector4f(1,1,1,1);
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Directional Light", nodeFlags);
        if (nodeOpen) {
            MarbleGui.colorEdit4("Color", color);
            intensity = MarbleGui.dragFloat("Intensity", intensity);
            ImGui.treePop();
        }
    }

    @Override
    public void renderGizmo()
    {

    }
}
