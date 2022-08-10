package marble.entity.components;

import marble.entity.Entity;
import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import physx.common.PxQuat;
import physx.common.PxVec3;
import physx.physics.*;
import physx.common.PxTransform;
import physx.common.PxIDENTITYEnum;
import physx.geomutils.PxBoxGeometry;

import marble.gui.MarbleGui;
import static marble.physics.Physics.physics;

public class RigidBody extends Component
{
    public PxRigidActor rigidActor;

    private final PxShape shape;
    private final PxMaterial material;
    private final PxBoxGeometry geometry;
    private final PxShapeFlags shapeFlags;

    private final Vector3f vGeo = new Vector3f();

    public RigidBody(Entity ent)
    {
        shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PxIDENTITYEnum.PxIdentity);
        var pos = new PxVec3(ent.transform.getPosition().x, ent.transform.getPosition().y, ent.transform.getPosition().z);
        var rot = new PxQuat();
        var test = new PxTransform(pos, rot);
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        geometry = new PxBoxGeometry(1f, 1f, 1f);
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(geometry, material, true, shapeFlags);
        rigidActor = physics.createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);
        rigidActor.attachShape(shape);

        tmpPose.destroy();
        tmpFilterData.destroy();
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
        if (nodeOpen)
        {
            MarbleGui.vec3Controller("Rb box transform", vGeo);
            ImGui.treePop();
        }

    }

}
