package marble.scene;

import java.util.*;

import org.joml.Vector3f;

import marble.util.Time;
import marble.renderer.Renderer;
import marble.editor.EditorLayer;
import marble.entity.Entity;
import marble.entity.components.Registry;
import marble.entity.components.Component;
import marble.entity.components.camera.Camera;
import marble.entity.components.camera.EditorCamera;

public class Scene {

    public transient final EditorCamera editorCamera = new EditorCamera();

    private String name;
    private transient float sceneStartedTime;
    private transient boolean isRunning = false;
    private transient final Renderer renderer = new Renderer();

    protected float specularPower = 10;
    protected transient Camera mainCamera;
    protected transient final Registry registry = new Registry();
    protected Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
    protected final Map<Integer, Entity> entities = new HashMap<>();

    public void init()
    {
    }

    public void update(float dt)
    {
    }

    public Scene(String name)
    {
        this.name = name;
    }

    public Scene(String name, float specularPower, Vector3f ambientLight)
    {
        this.name = name;
        this.specularPower = specularPower;
        this.ambientLight = ambientLight;
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
        for (Entity entity : entities.values())
            entity.update(dt);
        update(dt);
    }

    public void onSceneRender()
    {
        renderer.render(editorCamera, registry, EditorLayer.EDITOR_FRAMEBUFFER, ambientLight, specularPower, Renderer.ViewportId.EDITOR);
        renderer.render(mainCamera, registry, EditorLayer.GAME_FRAMEBUFFER, ambientLight, specularPower, Renderer.ViewportId.GAME);
    }

    protected Entity createEntity(String tag)
    {
        return new Entity(tag);
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
            if (component instanceof Camera)
                mainCamera = (Camera) component;
            registry.register(component);
        }
    }

    protected float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    public Camera getEditorCamera()
    {
        return editorCamera;
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

}
