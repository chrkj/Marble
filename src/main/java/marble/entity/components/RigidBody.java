package marble.entity.components;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import physx.physics.*;
import physx.common.PxVec3;
import physx.common.PxTransform;
import physx.common.PxIDENTITYEnum;
import physx.geomutils.PxBoxGeometry;

import marble.gui.MarbleGui;
import marble.entity.Entity;

import static marble.physics.Physics.physics;

public class RigidBody extends Component
{
    public boolean isStatic;
    public PxRigidActor rigidActor;

    private final PxShape shape;
    private final PxMaterial material;
    private final PxBoxGeometry geometry;
    private final PxShapeFlags shapeFlags;

    private final Vector3f vGeo = new Vector3f();

    public RigidBody(Entity ent, boolean isStatic)
    {
        try (MemoryStack mem = MemoryStack.stackPush())
        {
            this.isStatic = isStatic;
            var flags = (byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE);
            shapeFlags = PxShapeFlags.createAt(mem, MemoryStack::nmalloc, flags);

            var pos = ent.transform.getPosition();
            var tmpVec = PxVec3.createAt(mem, MemoryStack::nmalloc, pos.x,pos.y,pos.z);
            var tmpPose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
            tmpPose.setP(tmpVec);

            var tmpFilterData = PxFilterData.createAt(mem, MemoryStack::nmalloc, 1, 1, 0, 0);

            var scale = ent.transform.getScale();
            geometry = new PxBoxGeometry(scale.x, scale.y, scale.z);
            material = physics.createMaterial(0.5f, 0.5f, 1f);
            shape = physics.createShape(geometry, material, true, shapeFlags);
            shape.setSimulationFilterData(tmpFilterData);

            if (isStatic)
                rigidActor = physics.createRigidStatic(tmpPose);
            else
                rigidActor = physics.createRigidDynamic(tmpPose);

            rigidActor.attachShape(shape);
        }
    }

    @Override
    public void cleanUp()
    {
        geometry.destroy();
        shapeFlags.destroy();
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Rigid Body", nodeFlags);
        MarbleGui.inputText("isStatic", Boolean.toString(isStatic));
        if (nodeOpen)
        {
            MarbleGui.vec3Controller("Rb box transform", vGeo);
            ImGui.treePop();
        }

    }

}
