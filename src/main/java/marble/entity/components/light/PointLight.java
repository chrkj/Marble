package marble.entity.components.light;

import org.joml.Vector4f;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.gui.MarbleGui;
import marble.renderer.BatchRendering.Renderer2D;

public class PointLight extends Light
{
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
            ImGui.checkbox("Show gizmo", shouldRenderGizmo);
            ImGui.treePop();
        }
    }

    @Override
    public void renderGizmo()
    {
        if (!shouldRenderGizmo.get()) return;

        var entPos = entity.transform.getPosition();
        var p0 = new Vector4f(0, +1,0, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p1 = new Vector4f(0, -1,0, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p2 = new Vector4f(+1,0, 0, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p3 = new Vector4f(-1,0, 0, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p4 = new Vector4f(0, 0, +1, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p5 = new Vector4f(0, 0, -1, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p6 = new Vector4f(+0.5f,+0.5f, +0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p7 = new Vector4f(-0.5f,-0.5f, -0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p8 = new Vector4f(+0.5f,+0.5f, -0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p9 = new Vector4f(-0.5f,-0.5f, +0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p10 = new Vector4f(-0.5f,+0.5f, +0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p11 = new Vector4f(+0.5f,-0.5f, -0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p12 = new Vector4f(-0.5f,+0.5f, -0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);
        var p13 = new Vector4f(+0.5f,-0.5f, +0.5f, 1).add(entPos.x, entPos.y, entPos.z, 0);

        Renderer2D.drawLine(p0, p1, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p2, p3, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p4, p5, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p6, p7, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p8, p9, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p10, p11, new Vector4f(1,1,0,1));
        Renderer2D.drawLine(p12, p13, new Vector4f(1,1,0,1));
    }
}
