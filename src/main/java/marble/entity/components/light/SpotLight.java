package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.gui.MarbleGui;
import org.joml.Vector3f;

public class SpotLight extends Light {

    // TODO: Fix spotlight not working

    private float cutOff = 140;
    private Vector3f coneDirection = new Vector3f(0, 0, -1);
    private PointLight pointLight = new PointLight();

    public final void setCutOffAngle(float cutOffAngle)
    {
        this.setCutOff((float) Math.cos(Math.toRadians(cutOffAngle)));
    }

    public float getCutOff()
    {
        return (float) Math.cos(Math.toRadians(cutOff));
    }

    public void setCutOff(float cutOff)
    {
        this.cutOff = (float) Math.cos(Math.toRadians(cutOff));
    }

    public Vector3f getConeDirection()
    {
        return coneDirection;
    }

    public PointLight getPointLight()
    {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight)
    {
        this.pointLight = pointLight;
    }

    public void setConeDirection(Vector3f coneDirection)
    {
        this.coneDirection = coneDirection;
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Spot Light", nodeFlags);
        if (nodeOpen) {
            MarbleGui.vec3Controller("Cone direction", coneDirection);
            cutOff = MarbleGui.dragFloat("CutOff", cutOff);
            pointLight.renderEntityInspector();
            ImGui.treePop();
        }
    }
}
