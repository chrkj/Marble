package marble.scene;

import java.util.List;
import java.util.ArrayList;

import marble.Window;
import marble.util.Time;
import marble.camera.Camera;
import marble.renderer.Renderer;
import marble.gameobject.GameObject;
import marble.gameobject.components.light.Light;
import marble.gameobject.components.light.SpotLight;
import marble.gameobject.components.light.PointLight;
import marble.gameobject.components.light.DirectionalLight;
import org.joml.Vector4f;

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
    protected Vector4f ambientLight = new Vector4f(0f, 0f, 0f, 0f);
    protected final List<Light> lights = new ArrayList<>();
    protected final List<GameObject> gameObjects = new ArrayList<>();

    private float sceneStartedTime;
    private boolean isRunning = false;

    public abstract void init();

    public void start()
    {
        sceneStartedTime = Time.getTime();
        for (GameObject gameObject : gameObjects) {
            gameObject.start();
            renderer.add(gameObject);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject)
    {
        if (!isRunning) {
            gameObjects.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            gameObject.start();
            renderer.add(gameObject);
        }
        // TODO
        if (gameObject.hasComponent(DirectionalLight.class))
            lights.add(gameObject.getComponent(DirectionalLight.class));
        if (gameObject.hasComponent(SpotLight.class))
            lights.add(gameObject.getComponent(SpotLight.class));
        if (gameObject.hasComponent(PointLight.class))
            lights.add(gameObject.getComponent(PointLight.class));
    }

    public void changeScene(Scene newScene)
    {
        Window.nextScene = newScene;
        Window.shouldChangeScene = true;
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public abstract void update(float dt);

    public float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    public void cleanUp()
    {
        for (GameObject gameObject : gameObjects)
            gameObject.cleanUp();
    }

    public Vector4f getAmbientLight()
    {
        return ambientLight;
    }

}
