package marble.entity.components;

import org.joml.Vector3f;
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
        entity.removeComponent(this);
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Rigid Body", nodeFlags);
        if (nodeOpen)
        {
            if (MarbleGui.ButtonCenteredOnLine("Delete", 0))
                cleanUp();
            ImGui.treePop();
        }

    }

}
