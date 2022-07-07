package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.gui.MarbleGui;

public class SpotLight extends Light {

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
        if (nodeOpen) {
            cutOff = MarbleGui.dragFloat("CutOff", cutOff);
            MarbleGui.colorEdit4("Color", color);
            intensity = MarbleGui.dragFloat("Intensity", intensity);
            pointLight.linear = MarbleGui.dragFloat("Linear", pointLight.linear);
            pointLight.constant = MarbleGui.dragFloat("Constant", pointLight.constant);
            pointLight.exponent = MarbleGui.dragFloat("Exponent", pointLight.exponent);
            ImGui.treePop();
        }
    }
}
