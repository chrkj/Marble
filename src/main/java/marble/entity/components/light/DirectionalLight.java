package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import org.joml.Vector4f;

import marble.gui.MarbleGui;
import marble.renderer.BatchRendering.Renderer2D;

public class DirectionalLight extends Light
{
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
        if (nodeOpen)
        {
            MarbleGui.colorEdit4("Color", color);
            intensity = MarbleGui.dragFloat("Intensity", intensity);
            ImGui.treePop();
        }
    }

    @Override
    public void renderGizmo()
    {
        var entPos = entity.transform.getPosition();
        var worldMatrix = entity.getWorldMatrix();

        var p0 = new Vector4f(entPos, 0);
        var p1 = new Vector4f(0, -gizmoLength, 0, 1).mul(worldMatrix);

        Renderer2D.drawLine(p0, p1, new Vector4f(1,1,0,1));

    }
}
