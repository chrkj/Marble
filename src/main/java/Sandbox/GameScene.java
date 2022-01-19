package sandbox;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import marble.Window;
import marble.scene.Scene;
import marble.camera.Camera;
import marble.gameobject.GameObject;
import marble.listeners.KeyListener;

public class GameScene extends Scene {

    public GameScene()
    {
        System.out.println("Loading Game scene...");
    }

    @Override
    public void init()
    {
        camera = new Camera(new Vector3f(0,0,10));
    }

    @Override
    public void update(float dt)
    {
        float camSpeed = 10;
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.move(0, camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.move(0, -camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.move(-camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.move(camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            camera.move(0,0, -camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            camera.move(0,0, camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            changeScene(new EditorScene());

        for (GameObject gameObject : gameObjects)
            gameObject.update(dt);

        renderer.render(camera, lights);

    }
}
