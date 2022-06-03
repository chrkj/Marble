package marble.scene;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import marble.Application;
import marble.editor.EditorLayer;
import marble.util.Time;
import marble.renderer.Renderer;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;
import marble.entity.Entity;
import marble.entity.components.Registry;
import marble.entity.components.Component;
import marble.entity.components.camera.Camera;
import marble.entity.components.camera.EditorCamera;

public abstract class Scene {

    private String name;
    private transient float sceneStartedTime;
    private transient boolean isRunning = false;
    private transient final EditorCamera editorCamera = new EditorCamera();
    private transient final Renderer renderer = new Renderer();

    protected float specularPower = 10;
    protected transient Camera mainCamera;
    protected transient final Registry registry = new Registry();
    protected final Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
    protected final List<Entity> entities = new ArrayList<>();

    public abstract void init();
    public abstract void update(float dt);

    public Scene(String name)
    {
        this.name = name;
    }

    public void start()
    {
        sceneStartedTime = Time.getTime();
        for (Entity entity : entities) {
            entity.start();
        }
        isRunning = true;
    }

    public void onUpdate(float dt)
    {
        handleSceneViewportInput(dt);
        for (Entity entity : entities)
            entity.update(dt);
        update(dt);
    }

    public void onRender()
    {
        // TODO: Make Renderer API to call from scene
        renderer.render(editorCamera, registry, EditorLayer.sceneViewportFramebuffer, ambientLight, specularPower, 1);
        renderer.render(mainCamera, registry, EditorLayer.gameViewportFramebuffer, ambientLight, specularPower, 0);
    }

    public void cleanUp()
    {
        for (Entity entity : entities)
            entity.cleanUp();
    }

    protected Entity createEntity()
    {
        return new Entity();
    }

    protected Entity createEntity(String tag)
    {
        return new Entity(tag);
    }

    protected void addEntityToScene(Entity entity)
    {
        if (!isRunning) {
            entities.add(entity);
        } else {
            entities.add(entity);
            entity.start();
        }
        for (Component component : entity.getAllComponents()) {
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

    public List<Entity> getEntities()
    {
        return entities;
    }

    private void handleSceneViewportInput(float dt)
    {
        if (EditorLayer.allowSceneViewportInput) {
            float camSpeed = 10 * dt;
            float camRotSpeed = 15 * dt;

            glfwSetInputMode(Application.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            editorCamera.rotate(-MouseListener.mouseDelta().x * camRotSpeed, -MouseListener.mouseDelta().y * camRotSpeed, 0);

            if (KeyListener.isKeyPressed(GLFW_KEY_W))
                editorCamera.move(0, 0, -camSpeed);
            if (KeyListener.isKeyPressed(GLFW_KEY_S))
                editorCamera.move(0, 0, camSpeed);
            if (KeyListener.isKeyPressed(GLFW_KEY_A))
                editorCamera.move(-camSpeed, 0, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_D))
                editorCamera.move(camSpeed, 0, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_E))
                editorCamera.move(0, -camSpeed, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_Q))
                editorCamera.move(0, camSpeed, 0);
        } else {
            glfwSetInputMode(Application.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    public String getName()
    {
        return name.replaceAll("\\s+", "_").toLowerCase();
    }

}
