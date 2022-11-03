package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import static org.lwjgl.opengl.GL11.*;

import marble.gui.MarbleGui;
import marble.renderer.BatchRendering.Renderer2D;
import org.joml.Vector4f;

public class SpotLight extends Light
{
    // TODO: Fix rotation to match entity direction properly

    private float cutOff = 140;
    private PointLight pointLight = new PointLight();

    public float getCutOff()
    {
        return (float) Math.cos(Math.toRadians(cutOff));
    }

    public void setCutOff(float cutOff)
    {
        this.cutOff = cutOff;
    }

    public PointLight getPointLight()
    {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight)
    {
        this.pointLight = pointLight;
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Spot Light", nodeFlags);
        if (nodeOpen)
        {
            cutOff = MarbleGui.dragFloat("CutOff", cutOff);
            MarbleGui.colorEdit4("Color", color);
            intensity = MarbleGui.dragFloat("Intensity", intensity);
            pointLight.linear = MarbleGui.dragFloat("Linear", pointLight.linear);
            pointLight.constant = MarbleGui.dragFloat("Constant", pointLight.constant);
            pointLight.exponent = MarbleGui.dragFloat("Exponent", pointLight.exponent);
            if (MarbleGui.ButtonCenteredOnLine("Delete", 0))
                cleanUp();
            ImGui.treePop();
        }
    }

    @Override
    public void renderGizmo()
    {
        // TODO: Add rotation specifically to the light so it doesn't rely on ent rot
        var entPos = entity.transform.getPosition();
        var p0 = new Vector4f(entPos, 0);
        var p1 = new Vector4f(0, -gizmoLength, 0, 1).mul(entity.getWorldMatrix());
        Renderer2D.drawLine(p0, p1, new Vector4f(1,1,0,1));
    }
}
