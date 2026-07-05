package marble.entity.components;

import org.joml.Vector4f;
import org.joml.Quaternionf;
import org.lwjgl.system.MemoryStack;

import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.flag.ImGuiTreeNodeFlags;

import physx.physics.*;
import physx.common.PxQuat;
import physx.common.PxVec3;
import physx.common.PxTransform;
import physx.geomutils.PxBoxGeometry;

import marble.gui.MarbleGui;
import marble.entity.Entity;
import marble.physics.Physics;
import marble.renderer.BatchRendering.Renderer2D;

public class RigidBody extends Component
{
    public boolean isStatic;
    public PxRigidActor rigidActor;

    private final ImBoolean shouldRenderCollider = new ImBoolean(true);

    public RigidBody(Entity ent, boolean isStatic)
    {
        try (MemoryStack mem = MemoryStack.stackPush())
        {
            this.isStatic = isStatic;
            var pos = ent.transform.getPosition();
            var rot = ent.transform.getRotation();
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

            // Same convention as Transform.getRotationMatrix(): R = Rz * Ry * Rx
            var quat = new Quaternionf()
                    .rotateZ((float) Math.toRadians(rot.z))
                    .rotateY((float) Math.toRadians(rot.y))
                    .rotateX((float) Math.toRadians(rot.x));
            var pxPos = PxVec3.createAt(mem, MemoryStack::nmalloc, pos.x, pos.y, pos.z);
            var pxRot = PxQuat.createAt(mem, MemoryStack::nmalloc, quat.x, quat.y, quat.z, quat.w);
            rigidActor.setGlobalPose(PxTransform.createAt(mem, MemoryStack::nmalloc, pxPos, pxRot));
        }
    }

    public void renderCollider()
    {
        if (shouldRenderCollider.get())
            Renderer2D.drawRect(this, new Vector4f(0,1,0,1));
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
            ImGui.checkbox("Show collider", shouldRenderCollider);
            if (MarbleGui.ButtonCenteredOnLine("Delete", 0))
                cleanUp();
            ImGui.treePop();
        }

    }

}
