package marble.Scene;

import marble.Camera.Camera;
import marble.GameObject;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    private boolean isRunning = false;
    protected Camera camera;
    protected List<GameObject> gameObjects = new ArrayList<>();

    public Scene()
    {
    }

    public void init()
    {

    }

    public void start()
    {
        for (GameObject gameObject : gameObjects) {
            gameObject.start();
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
        }
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public abstract void update(float dt);

}
