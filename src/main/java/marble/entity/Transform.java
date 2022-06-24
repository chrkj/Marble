package marble.entity;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import org.joml.Vector3f;

import marble.gui.MarbleGui;

public class Transform {

    private Vector3f scale;
    private Vector3f position;
    private Vector3f rotation;

    public Transform()
    {
        init(new Vector3f(), new Vector3f(), new Vector3f(1,1,1));
    }

    public Transform(Vector3f position, Vector3f rotation)
    {
        init(position, rotation, new Vector3f(1,1,1));
    }

    public Transform(Vector3f position)
    {
        init(position, new Vector3f(), new Vector3f(1,1,1));
    }

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        init(position, rotation, scale);
    }

    private void init(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        this.scale = scale;
        this.position = position;
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z)
    {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void incrementRotation(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;
    }

    public void setPosition(float x, float y, float z)
    {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setScale(float x, float y, float z)
    {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }

    protected void setupInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Transform", nodeFlags);
        if (nodeOpen) {
            MarbleGui.vec3Controller("Position", position);
            MarbleGui.vec3Controller("Rotation", rotation);
            MarbleGui.vec3Controller("Scale", scale);
            ImGui.treePop();
        }
    }

    public Vector3f getScale()
    {
        return scale;
    }

    public void setScale(Vector3f scale)
    {
        this.scale = scale;
    }

    public Vector3f getPosition()
    {
        return position;
    }

    public void setPosition(Vector3f position)
    {
        this.position = position;
    }

    public Vector3f getRotation()
    {
        return rotation;
    }

    public void setRotation(Vector3f rotation)
    {
        this.rotation = rotation;
    }

}
