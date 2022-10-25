package marble.physics;

import org.lwjgl.system.MemoryStack;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.physics.*;
import physx.cooking.PxCooking;
import physx.cooking.PxCookingParams;
import physx.geomutils.PxGeometry;
import physx.geomutils.PxBoxGeometry;
import physx.geomutils.PxPlaneGeometry;
import physx.extensions.PxDefaultAllocator;

public class Physics
{
    public static final PxPhysics physics;
    public static final PxCooking cooking;
    public static final PxFoundation foundation;
    public static final PxMaterial defaultMaterial;
    public static final PxFilterData defaultFilterData;
    public static final int PX_PHYSICS_VERSION = PxTopLevelFunctions.getPHYSICS_VERSION();

    private static final int numThreads = 8;

    static
    {
        // PhysX foundation object
        PxErrorCallback errorCb = new PxDefaultErrorCallback();
        PxDefaultAllocator allocator = new PxDefaultAllocator();
        foundation = PxTopLevelFunctions.CreateFoundation(PX_PHYSICS_VERSION, allocator, errorCb);

        // PhysX main physics object
        PxTolerancesScale tolerances = new PxTolerancesScale();
        physics = PxTopLevelFunctions.CreatePhysics(PX_PHYSICS_VERSION, foundation, tolerances);
        defaultMaterial = physics.createMaterial(0.3f, 0.3f, 0.3f);
        defaultFilterData = new PxFilterData(0, 0, 0, 0);
        defaultFilterData.setWord0(1);
        defaultFilterData.setWord1(0xffffffff);
        defaultFilterData.setWord2(0);
        defaultFilterData.setWord3(0);

        var cookingParams = new PxCookingParams(tolerances);
        cooking = PxTopLevelFunctions.CreateCooking(PX_PHYSICS_VERSION, foundation, cookingParams);

        PxTopLevelFunctions.InitExtensions(physics);
    }

    private Physics() { }

    public static PxScene createPhysicsScene()
    {
        try (var mem = MemoryStack.stackPush())
        {
            PxSceneDesc sceneDesc = PxSceneDesc.createAt(mem, MemoryStack::nmalloc, physics.getTolerancesScale());
            sceneDesc.setGravity(new PxVec3(0f, -9.81f, 0f));
            sceneDesc.setCpuDispatcher(PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads));
            sceneDesc.setFilterShader(PxTopLevelFunctions.DefaultFilterShader());
            return physics.createScene(sceneDesc);
        }
    }

    public static PxRigidDynamic createDefaultBox(float posX, float posY, float posZ, float sizeX, float sizeY, float sizeZ)
    {
        return createDefaultBox(posX, posY, posZ, sizeX, sizeY, sizeZ, defaultFilterData);
    }

    public static PxRigidDynamic createDefaultBox(float posX, float posY, float posZ, float sizeX, float sizeY, float sizeZ, PxFilterData simFilterData)
    {
        try (MemoryStack mem = MemoryStack.stackPush())
        {
            PxBoxGeometry box = PxBoxGeometry.createAt(mem, MemoryStack::nmalloc, sizeX, sizeY, sizeZ);
            PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
            pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, posX, posY, posZ));

            PxShape shape = physics.createShape(box, defaultMaterial, true);
            PxRigidDynamic body = physics.createRigidDynamic(pose);
            shape.setSimulationFilterData(simFilterData);
            body.attachShape(shape);
            return body;
        }
    }

    public static PxRigidStatic createGroundPlane(float posX, float posY, float posZ)
    {
        try (MemoryStack mem = MemoryStack.stackPush())
        {
            PxPlaneGeometry plane = PxPlaneGeometry.createAt(mem, MemoryStack::nmalloc);
            PxShape shape = physics.createShape(plane, defaultMaterial, true);

            float r = 1f / (float) Math.sqrt(2f);
            PxQuat q = PxQuat.createAt(mem, MemoryStack::nmalloc, 0f, 0f, r, r);
            PxVec3 p = PxVec3.createAt(mem, MemoryStack::nmalloc, 0, 0, 0);
            shape.setLocalPose(PxTransform.createAt(mem, MemoryStack::nmalloc, p, q));
            return createStaticBody(shape, posX, posY, posZ);
        }
    }

    public static PxRigidStatic createStaticBody(PxGeometry fromGeometry, float posX, float posY, float posZ)
    {
        PxShape shape = physics.createShape(fromGeometry, defaultMaterial, true);
        shape.setSimulationFilterData(defaultFilterData);
        return createStaticBody(shape, posX, posY, posZ);
    }

    public static PxRigidStatic createStaticBody(PxShape fromShape, float posX, float posY, float posZ)
    {
        try (MemoryStack mem = MemoryStack.stackPush())
        {
            PxTransform pose = PxTransform.createAt(mem, MemoryStack::nmalloc, PxIDENTITYEnum.PxIdentity);
            pose.setP(PxVec3.createAt(mem, MemoryStack::nmalloc, posX, posY, posZ));

            PxRigidStatic body = physics.createRigidStatic(pose);
            body.attachShape(fromShape);
            return body;
        }
    }

}
