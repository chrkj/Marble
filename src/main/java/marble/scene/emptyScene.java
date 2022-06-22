package marble.scene;

import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.collision.shapes.StaticPlaneShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;

import marble.editor.ConsolePanel;
import marble.entity.components.camera.PerspectiveCamera;
import marble.physics.Physics;
import marble.util.Loader;
import marble.entity.Entity;
import marble.entity.components.light.LightType;
import marble.entity.components.light.LightFactory;

import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class emptyScene extends Scene {

    private Entity helmet;
    private DiscreteDynamicsWorld world;
    private RigidBody fallRigidBody;

    public emptyScene(String name)
    {
        super(name);
    }

    @Override
    public void init()
    {
        world = Physics.CreateWorld();
        ambientLight.set(0.1f);
        {
            Entity entity = createEntity("Main Camera")
                    .setPosition(3f, -2.5f, 35f)
                    .addComponent(new PerspectiveCamera());
            addEntityToScene(entity);
        }
        {
            Entity entity = createEntity("Helmet")
                    .setPosition(3,-2,3)
                    .addComponent(Loader.loadMeshObj("assets/obj/helm.obj"));
            Entity child1 = createEntity("ChildEntity1");
            Entity child2 = createEntity("ChildEntity2");
            Entity child3 = createEntity("ChildEntity3");
            child1.setChild(child2);
            entity.setChild(child1);
            entity.setChild(child3);
            addEntityToScene(entity);
            helmet = entity;
        }
        {
            Entity entity = createEntity("Directional light")
                    .setPosition(-1,2,4)
                    .setRotation(333,53,0)
                    .addComponent(LightFactory.getLight(LightType.DIRECTIONAL));
            addEntityToScene(entity);
        }


        // setup our collision shapes
        CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 1);
        CollisionShape fallShape = new SphereShape(1);
        // setup the motion state
        DefaultMotionState groundMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, -1, 0), 1.0f)));
        RigidBodyConstructionInfo groundRigidBodyCI = new RigidBodyConstructionInfo(0, groundMotionState, groundShape, new Vector3f(0,0,0));
        RigidBody groundRigidBody = new RigidBody(groundRigidBodyCI);
        world.addRigidBody(groundRigidBody); // add our ground to the dynamic world..

        // setup the motion state for the ball
        DefaultMotionState fallMotionState = new DefaultMotionState(new Transform(new Matrix4f(new Quat4f(0, 0, 0, 1), new Vector3f(0, 50, 0), 1.0f)));
        //This we're going to give mass so it responds to gravity
        int mass = 1;
        Vector3f fallInertia = new Vector3f(0,0,0);
        fallShape.calculateLocalInertia(mass,fallInertia);
        RigidBodyConstructionInfo fallRigidBodyCI = new RigidBodyConstructionInfo(mass,fallMotionState,fallShape,fallInertia);
        fallRigidBody = new RigidBody(fallRigidBodyCI);
        //now we add it to our physics simulation
        world.addRigidBody(fallRigidBody);

    }

    @Override
    public void update(float dt)
    {
        world.stepSimulation(dt);
        Transform trans = new Transform();
        fallRigidBody.getMotionState().getWorldTransform(trans);
        ConsolePanel.log("sphere height: " + trans.origin.y);
        helmet.transform.position.y = trans.origin.y;
    }

}
