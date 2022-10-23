package marble.entity.components;

import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import physx.common.PxVec3;
import physx.physics.*;
import physx.common.PxTransform;
import physx.common.PxIDENTITYEnum;
import physx.geomutils.PxBoxGeometry;

import marble.gui.MarbleGui;
import marble.entity.Entity;
import static marble.physics.Physics.physics;

public class RigidBody extends Component
{
    public PxRigidActor rigidActor;
    public boolean isStatic;

    private final PxShape shape;
    private final PxMaterial material;
    private final PxBoxGeometry geometry;
    private final PxShapeFlags shapeFlags;

    private final Vector3f vGeo = new Vector3f();

    public RigidBody(Entity ent, boolean isStatic)
    {
        this.isStatic = isStatic;
        shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PxIDENTITYEnum.PxIdentity);
        var tmpVec = new PxVec3();
        tmpVec.setX(ent.transform.getPosition().x);
        tmpVec.setY(ent.transform.getPosition().y);
        tmpVec.setZ(ent.transform.getPosition().z);
        tmpPose.setP(tmpVec);
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        geometry = new PxBoxGeometry(1f, 1f, 1f);
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(geometry, material, true, shapeFlags);

        if (isStatic)
            rigidActor = physics.createRigidStatic(tmpPose);
        else
            rigidActor = physics.createRigidDynamic(tmpPose);

        shape.setSimulationFilterData(tmpFilterData);
        rigidActor.attachShape(shape);

        tmpVec.destroy();
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
