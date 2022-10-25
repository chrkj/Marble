package marble.entity.components;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import physx.geomutils.PxBoxGeometry;
import physx.physics.*;

import marble.gui.MarbleGui;
import marble.entity.Entity;
import marble.physics.Physics;

public class RigidBody extends Component
{
    public boolean isStatic;
    public PxRigidActor rigidActor;

    private final Vector3f vGeo = new Vector3f();

    public RigidBody(Entity ent, boolean isStatic)
    {
        try (MemoryStack mem = MemoryStack.stackPush())
        {
            this.isStatic = isStatic;
            var pos = ent.transform.getPosition();
            var scale = ent.transform.getScale();
            if (isStatic)
            {
                var geo = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, scale.x, scale.y, scale.z);
                rigidActor = Physics.createStaticBody(geo, pos.x, pos.y, pos.z);
            }
            else
            {
                rigidActor = Physics.createDefaultBox(pos.x, pos.y, pos.z, scale.x, scale.y, scale.z);
            }
        }
    }

    @Override
    public void cleanUp()
    {
        rigidActor.release();
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Rigid Body", nodeFlags);
        if (nodeOpen)
        {
            MarbleGui.inputText("isStatic", Boolean.toString(isStatic));
            var pos = rigidActor.getGlobalPose().getP();
            vGeo.x = pos.getX();
            vGeo.y = pos.getY();
            vGeo.z = pos.getZ();
            var rot = rigidActor.getGlobalPose().getQ();
            MarbleGui.vec3Controller("Rb box pos", vGeo);
            MarbleGui.vec4Controller("Rb box rot", new Vector4f(rot.getX(), rot.getY(), rot.getZ(), rot.getW()));
            ImGui.treePop();
        }

    }

}
