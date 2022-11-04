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
            ImGui.checkbox("Show gizmo", shouldRenderGizmo);
            ImGui.treePop();
        }
    }

    @Override
    public void renderGizmo()
    {
        if (!shouldRenderGizmo.get()) return;

        var entPos = entity.transform.getPosition();
        var worldMatrix = entity.getWorldMatrix();

        var p0 = new Vector4f(entPos, 0);
        var p1 = new Vector4f(0, -gizmoLength, 0, 1).mul(worldMatrix);
        var p2 = new Vector4f(-1, 0, 0, 1).mul(worldMatrix);
        var p3 = new Vector4f(-1, -gizmoLength, 0, 1).mul(worldMatrix);
        var p4 = new Vector4f(1, 0, 0, 1).mul(worldMatrix);
        var p5 = new Vector4f(1, -gizmoLength, 0, 1).mul(worldMatrix);
        var p6 = new Vector4f(0, 0, 1, 1).mul(worldMatrix);
        var p7 = new Vector4f(0, -gizmoLength, 1, 1).mul(worldMatrix);
        var p8 = new Vector4f(0, 0, -1, 1).mul(worldMatrix);
        var p9 = new Vector4f(0, -gizmoLength, -1, 1).mul(worldMatrix);

        Renderer2D.drawLine(p0, p1, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p2, p3, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p4, p5, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p6, p7, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p8, p9, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p2, p6, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p6, p4, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p4, p8, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p8, p2, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p2, p4, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p6, p8, new Vector4f(1,1,0,1));
    }
}
