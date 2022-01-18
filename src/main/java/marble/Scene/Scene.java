package marble.scene;

import java.util.List;
import java.util.ArrayList;

import marble.Window;
import marble.util.Time;
import marble.camera.Camera;
import marble.renderer.Renderer;
import marble.gameobject.GameObject;
import marble.gameobject.components.Component;

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
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
        for (GameObject gameObject : gameObjects) {
            for (Component component : gameObject.getAllComponents())
                component.cleanUp();
            gameObject.cleanUp();
        }
    }
}
