package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import static org.lwjgl.opengl.GL11.*;

import marble.gui.MarbleGui;

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
        var pos = entity.transform.getPosition();
        glLineWidth(2);
        glBegin(GL_LINES);
            glVertex3f(pos.x, pos.y, pos.z);
            glVertex3f(pos.x + 10, pos.y + 10, pos.z + 10);
        glEnd();
    }
}
