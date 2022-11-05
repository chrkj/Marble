package marble.scene;

import java.util.*;

import org.joml.Vector3f;

import physx.physics.PxScene;

import marble.util.Time;
import marble.renderer.Renderer;
import marble.editor.EditorLayer;
import marble.physics.Physics;
import marble.entity.Entity;
import marble.entity.components.Registry;
import marble.entity.components.RigidBody;
import marble.entity.components.Component;
import marble.entity.components.camera.Camera;
import marble.entity.components.camera.EditorCamera;

public class Scene
{
    public EditorCamera editorCamera = new EditorCamera();

    private String name;
    private float specularPower;
    private Vector3f ambientLight;
    private final Map<Integer, Entity> entities = new HashMap<>();

    private transient Camera mainCamera;
    private transient float sceneStartedTime;
    private transient boolean isRunning = false;
    private transient final PxScene physicsScene;
    private transient final Renderer renderer = new Renderer();
    private transient final Registry registry = new Registry();

    public Scene(String name, float specularPower, Vector3f ambientLight)
    {
        this.name = name;
        this.specularPower = specularPower;
        this.ambientLight = ambientLight;
        this.physicsScene = Physics.createPhysicsScene();
    }

    public void start()
    {
        sceneStartedTime = Time.getTime();
        for (Entity entity : entities.values())
            entity.start();
        isRunning = true;
    }

    public void onSceneUpdate(float dt)
    {
        editorCamera.onUpdate(dt);
        if (!EditorLayer.sceneRunning) return;

        physicsScene.simulate(dt);
        physicsScene.fetchResults(true);
        for (Entity entity : entities.values())
            entity.update(dt);
    }

    public void onSceneRender()
    {
        renderer.shadowPass(registry);
        renderer.render(editorCamera, registry, EditorLayer.editorViewportFb, ambientLight, specularPower, Renderer.ViewportId.EDITOR);
        renderer.render(mainCamera, registry, EditorLayer.gameViewportFb, ambientLight, specularPower, Renderer.ViewportId.GAME);
    }

    public void addEntityToScene(Entity entity)
    {
        if (!isRunning)
        {
            entities.put(entity.getUuid(), entity);
        }
        else
        {
            entities.put(entity.getUuid(), entity);
            entity.start();
        }

        for (Component component : entity.getAllComponents())
        {
            if (component instanceof Camera camera)
                mainCamera = camera;
            else if (component instanceof RigidBody rigidBody)
                physicsScene.addActor(rigidBody.rigidActor);
            registry.register(component);
        }
    }

    protected float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    public Collection<Entity> getEntities()
    {
        return entities.values();
    }

    public String getSaveName()
    {
        return name.replaceAll("\\s+", "_").toLowerCase();
    }

    public void cleanUp()
    {
        for (Entity entity : entities.values())
            entity.cleanUp();
    }

    public Entity getEntityFromUUID(int uuid)
    {
        return entities.get(uuid);
    }

    public Registry getRegistry()
    {
        return registry;
    }

    public PxScene getPhysicsScene()
    {
        return physicsScene;
    }


}
