package marble.scene;

import java.util.List;
import java.util.ArrayList;
import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import marble.Window;
import marble.util.Time;
import marble.camera.Camera;
import marble.renderer.Renderer;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;
import marble.entity.Entity;
import marble.entity.components.light.Light;
import marble.entity.components.light.SpotLight;
import marble.entity.components.light.PointLight;
import marble.entity.components.light.DirectionalLight;

import game.GameScene;

public abstract class Scene {

    private float sceneStartedTime;
    private boolean isRunning = false;
    private final Renderer renderer = new Renderer();

    protected Camera editorCam = new Camera();
    protected float specularPower = 10;
    protected final List<Light> lights = new ArrayList<>();
    protected final List<Entity> entities = new ArrayList<>();
    protected final Vector3f ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);

    public abstract void init();
    public abstract void update(float dt);

    public void start()
    {
        sceneStartedTime = Time.getTime();
        for (Entity entity : entities) {
            entity.start();
            renderer.add(entity);
        }
        isRunning = true;
    }

    public void updateScene(float dt)
    {
        handleEditorInput(dt);
        for (Entity entity : entities)
            entity.update(dt);
        update(dt);
        renderer.render(editorCam, lights);
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
            renderer.add(entity);
        }

        if (entity.hasComponent(SpotLight.class))
            lights.add(entity.getComponent(SpotLight.class));
        if (entity.hasComponent(PointLight.class))
            lights.add(entity.getComponent(PointLight.class));
        if (entity.hasComponent(DirectionalLight.class))
            lights.add(entity.getComponent(DirectionalLight.class));
    }

    protected void changeScene(Scene newScene)
    {
        Window.nextScene = newScene;
        Window.shouldChangeScene = true;
    }

    protected float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    protected Camera getEditorCam()
    {
        return editorCam;
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

    private void handleEditorInput(float dt)
    {
        // TODO: Only allow input when viewport is active
        float camSpeed = 10 * dt;
        float camRotSpeed = 15 * dt;
        // Movement
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            editorCam.move(0,0, -camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            editorCam.move(0,0, camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            editorCam.move(-camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            editorCam.move(camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            editorCam.move(0, -camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            editorCam.move(0, camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            changeScene(new GameScene());
        if(KeyListener.isKeyPressed(KeyEvent.VK_1))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if(KeyListener.isKeyPressed(KeyEvent.VK_2))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (glfwGetInputMode(Window.windowPtr, GLFW_CURSOR) == GLFW_CURSOR_DISABLED)
            editorCam.rotate(-MouseListener.mouseDelta().x * camRotSpeed, -MouseListener.mouseDelta().y * camRotSpeed, 0);
    }

}
