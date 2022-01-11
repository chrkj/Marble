package Marble.Scene;

import Marble.Camera.Camera;
import Marble.GameObject.Components.Mesh;
import Marble.GameObject.GameObject;
import Marble.Renderer.Renderer;
import Marble.util.Time;

import java.util.List;
import java.util.ArrayList;

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
    protected final List<GameObject> gameObjects = new ArrayList<>();

    private float sceneStartedTime;
    private boolean isRunning = false;

    public abstract void init() throws Exception;

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

    public boolean isRunning()
    {
        return isRunning;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public abstract void update(float dt) throws Exception;

    public float timeSinceSceneStarted()
    {
        return Time.getTime() - sceneStartedTime;
    }

    public void cleanUp()
    {
        renderer.cleanUp();
        for (GameObject gameObject : gameObjects)
            if (gameObject.getComponent(Mesh.class) != null) {
                gameObject.getComponent(Mesh.class).cleanUp();
            }
    }
}
