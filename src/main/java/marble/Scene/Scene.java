package marble.Scene;

import marble.Camera.Camera;
import marble.GameObject;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Camera camera;
    protected Renderer renderer = new Renderer();
    protected List<GameObject> gameObjects = new ArrayList<>();

    private boolean isRunning = false;

    public void init()
    {
    }

    public void start()
    {
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

    public abstract void update(float dt);

}
