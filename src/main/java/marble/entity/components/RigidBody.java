package marble.entity.components;

import org.joml.Vector3f;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import physx.physics.*;
import physx.common.PxTransform;
import physx.common.PxIDENTITYEnum;
import physx.geomutils.PxBoxGeometry;

import marble.gui.MarbleGui;

import static marble.physics.Physics.physics;

public class RigidBody extends Component
{
    public PxRigidActor rigidActor;

    private PxShape shape;
    private PxMaterial material;
    private PxBoxGeometry geometry;
    private PxShapeFlags shapeFlags;

    private Vector3f vGeo = new Vector3f();

    public RigidBody()
    {
        shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));
        PxTransform tmpPose = new PxTransform(PxIDENTITYEnum.PxIdentity);
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        geometry = new PxBoxGeometry(1f, 1f, 1f);
        material = physics.createMaterial(0.5f, 0.5f, 0.5f);
        shape = physics.createShape(geometry, material, true, shapeFlags);
        rigidActor = physics.createRigidDynamic(tmpPose);
        shape.setSimulationFilterData(tmpFilterData);
        rigidActor.attachShape(shape);
    }

    @Override
    public void cleanUp()
    {

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
