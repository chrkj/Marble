package marble.scene;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import marble.Application;
import marble.EditorLayer;
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

    private float sceneStartedTime;
    private boolean isRunning = false;
    private final EditorCamera editorCamera = new EditorCamera();
    private final Renderer renderer = new Renderer();

    protected Camera mainCamera;
    protected float specularPower = 10;
    protected final Registry registry = new Registry();
    protected final List<Entity> entities = new ArrayList<>();
    protected final Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

    public abstract void init();
    public abstract void update(float dt);

    public void start()
    {
        sceneStartedTime = Time.getTime();
        for (Entity entity : entities) {
            entity.start();
        }
        isRunning = true;
    }

    public void updateScene(float dt)
    {
        handleSceneViewportInput(dt);
        for (Entity entity : entities)
            entity.update(dt);
        update(dt);
        renderer.render(editorCamera, registry, EditorLayer.sceneViewportFramebuffer, 1);
        if (mainCamera != null)
            renderer.render(mainCamera, registry, EditorLayer.gameViewportFramebuffer, 0);
    }

    public void cleanUp()
    {
        for (Entity entity : entities)
            entity.cleanUp();
    }

    public Vector3f getAmbientLight()
    {
        return ambientLight;
    }

    public float getSpecularPower()
    {
        return specularPower;
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

    protected void changeScene(Scene newScene)
    {
        // TODO: Scene manager
    }

    protected float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    protected Camera getEditorCamera()
    {
        return editorCamera;
    }

    protected void setSpecularPower(float specularPower)
    {
        this.specularPower = specularPower;
    }

    protected void setAmbientLight(float r, float g, float b, float a)
    {
        ambientLight.x = r;
        ambientLight.y = g;
        ambientLight.z = b;
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

    public List<Entity> getSceneEntities()
    {
        return entities;
    }
}
