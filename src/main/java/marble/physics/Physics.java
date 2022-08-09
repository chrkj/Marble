package marble.physics;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.extensions.PxDefaultAllocator;
import physx.geomutils.PxBoxGeometry;
import physx.physics.*;

public class Physics
{
    public static PxPhysics physics;

    private static PxTolerancesScale tolerances;

    public static void Init()
    {
        // get PhysX library version
        int version = PxTopLevelFunctions.getPHYSICS_VERSION();

        // create PhysX foundation object
        PxDefaultAllocator allocator = new PxDefaultAllocator();
        PxDefaultErrorCallback errorCb = new PxDefaultErrorCallback();
        PxFoundation foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb);

        // create PhysX main physics object
        tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances);
    }

    public static PxScene createPhysicsScene()
    {
        // create a physics scene
        int numThreads = 4;
        var gravity = new PxVec3(0f, -9.81f, 0f);
        var sceneDesc = new PxSceneDesc(tolerances);
        sceneDesc.setGravity(gravity);
        sceneDesc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads));
        sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
        return physics.createScene(sceneDesc);
    }

    public Physics()
    {
        var scene = createPhysicsScene();

        // create a default material
        PxMaterial material = physics.createMaterial(0.5f, 0.5f, 0.5f);

        // create default simulation shape flags
        PxShapeFlags shapeFlags = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE | PxShapeFlagEnum.eSIMULATION_SHAPE));

        // create a few temporary objects used during setup
        PxTransform tmpPose = new PxTransform(PxIDENTITYEnum.PxIdentity);
        PxFilterData tmpFilterData = new PxFilterData(1, 1, 0, 0);

        // create a large static box with size 20x1x20 as ground
        PxBoxGeometry groundGeometry = new PxBoxGeometry(10f, 0.5f, 10f);   // PxBoxGeometry uses half-sizes
        PxShape groundShape = physics.createShape(groundGeometry, material, true, shapeFlags);
        PxRigidStatic ground = physics.createRigidStatic(tmpPose);
        groundShape.setSimulationFilterData(tmpFilterData);
        ground.attachShape(groundShape);
        scene.addActor(ground);

        // create a small dynamic box with size 1x1x1, which will fall on the ground
        var tmpVec = new PxVec3(0f, 5f, 0f);

        tmpPose.setP(tmpVec);
        PxBoxGeometry boxGeometry = new PxBoxGeometry(0.5f, 0.5f, 0.5f);   // PxBoxGeometry uses half-sizes
        PxShape boxShape = physics.createShape(boxGeometry, material, true, shapeFlags);
        PxRigidDynamic box = physics.createRigidDynamic(tmpPose);
        boxShape.setSimulationFilterData(tmpFilterData);
        box.attachShape(boxShape);
        scene.addActor(box);

        // clean up temp objects
        groundGeometry.destroy();
        boxGeometry.destroy();
        tmpFilterData.destroy();
        tmpPose.destroy();
        tmpVec.destroy();
        shapeFlags.destroy();
        //sceneDesc.destroy();
        tolerances.destroy();

        // box starts at a height of 5
        float boxHeight = box.getGlobalPose().getP().getY();

        // run physics simulation
        for (int i = 0; i <= 500; i++) {
            scene.simulate(1f/60f);
            scene.fetchResults(true);

            boxHeight = box.getGlobalPose().getP().getY();
            if (i % 10 == 0)
                System.out.println("Step " + i + ": h = " + boxHeight);
        }

        // cleanup stuff
        scene.removeActor(ground);
        ground.release();
        groundShape.release();

        scene.removeActor(box);
        box.release();
        boxShape.release();

        scene.release();
        material.release();
        physics.release();
    }
}
