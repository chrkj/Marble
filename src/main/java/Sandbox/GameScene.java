package sandbox;

import marble.camera.Camera;
import marble.gameobject.components.Mesh;
import marble.gameobject.components.Texture;
import marble.gameobject.GameObject;
import marble.gameobject.Transform;
import marble.listeners.KeyListener;
import marble.scene.Scene;
import marble.Window;
import org.joml.Vector3f;

import java.awt.event.KeyEvent;

public class GameScene extends Scene {

    public GameScene()
    {
        System.out.println("Inside game scene.");
    }

    @Override
    public void init() throws Exception
    {
        camera = new Camera(new Vector3f(0,0,10));

        float[] positions = new float[] {
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, 0.5f, -0.5f,
                0.5f, 0.5f, -0.5f,
                -0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, 0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
                -0.5f, 0.5f, 0.5f,
                -0.5f, -0.5f, 0.5f,
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f, 0.5f,
                0.5f, -0.5f, 0.5f,
        };

        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.0f,
                0.5f, 0.5f,
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };

        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2,
                8, 10, 11, 9, 8, 11,
                12, 13, 7, 5, 12, 7,
                14, 15, 6, 4, 14, 6,
                16, 18, 19, 17, 16, 19,
                4, 6, 7, 5, 4, 7,
        };

        Texture texture = new Texture("assets/textures/grassblock.png");
        {
            GameObject go = new GameObject("Cube1", new Transform(new Vector3f(1.5f,1.2f,1), new Vector3f(30, 78, 10), 1));
            go.addComponent(new Mesh(positions, textCoords, indices, texture));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Cube2", new Transform(new Vector3f(1,2,4), new Vector3f(66, 5, 17), 1));
            go.addComponent(new Mesh(positions, textCoords, indices, texture));
            addGameObjectToScene(go);
        }
    }

    @Override
    public void update(float dt) throws Exception
    {
        float camSpeed = 10;
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.move(0, camSpeed,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.move(0, -camSpeed,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.move(-camSpeed,0,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.move(camSpeed,0,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            camera.move(0,0, -camSpeed, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            camera.move(0,0, camSpeed, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            Window.changeScene(0);

        for (GameObject gameObject : gameObjects)
            gameObject.update(dt);

        for (GameObject gameObject : gameObjects) {
            float rotation = gameObject.transform.rotation.z + 0.5f;
            if (rotation > 360)
                rotation = 0;
            gameObject.transform.setRotation(rotation, rotation, rotation);
        }

        renderer.render(camera);
    }
}
