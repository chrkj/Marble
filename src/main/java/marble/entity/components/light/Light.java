package marble.entity.components.light;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import marble.imgui.ImGuiLayer;
import org.joml.Vector4f;

import marble.entity.components.Component;

public abstract class Light extends Component {

    protected Vector4f color;
    protected float intensity;

    @Override
    public void cleanUp()
    {
    }

    public Vector4f getColor()
    {
        return color;
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        this.color.w = a;
    }

    public void setColor(Vector4f color)
    {
        this.color = color;
    }

    public float getIntensity()
    {
        return intensity;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    @Override
    public void setupInspector()
    {
        // TODO: Make Light a single component with options
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Directional Light", nodeFlags);
        if (nodeOpen) {
            ImGuiLayer.colorEdit4("Color", color);
            intensity = ImGuiLayer.dragFloat("Intensity", intensity);
            ImGui.treePop();
        }
    }

}
