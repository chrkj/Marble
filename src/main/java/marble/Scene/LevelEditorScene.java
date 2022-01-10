package marble.Scene;

import marble.Camera.Camera;
import marble.Components.Component;
import marble.Components.Mesh;
import marble.Components.SpriteRenderer;
import marble.GameObject;
import marble.Listeners.KeyListener;
import marble.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.Renderer;

import java.awt.event.KeyEvent;
import java.lang.reflect.Type;

public class LevelEditorScene extends Scene {

    public LevelEditorScene()
    {
        System.out.println("Inside level editor scene.");
    }

    @Override
    public void init()
    {
        camera = new Camera(new Vector3f(0,0,10));

        float[] positions = new float[] {
                -0.5f,  0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f,
                 0.5f, -0.5f,  0.5f,
                 0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,
                 0.5f,  0.5f, -0.5f,
                -0.5f, -0.5f, -0.5f,
                 0.5f, -0.5f, -0.5f,
        };
        int[] indices = new int[] {
                0, 1, 3, 3, 1, 2,
                4, 0, 3, 5, 4, 3,
                3, 2, 7, 5, 3, 7,
                6, 1, 0, 6, 0, 4,
                2, 1, 6, 2, 6, 7,
                7, 6, 4, 7, 4, 5,
        };
        float[] colors = new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
        {
            GameObject go = new GameObject("Cube1", new Transform(new Vector3f(), new Vector3f(30, 78, 10), 1));
            go.addComponent(new Mesh(positions, colors, indices));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Cube2", new Transform(new Vector3f(2,2,2), new Vector3f(66, 5, 17), 1));
            go.addComponent(new Mesh(positions, colors, indices));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Cube2", new Transform(new Vector3f(-2,-2,-2), new Vector3f(66, 5, 17), 1));
            go.addComponent(new Mesh(positions, colors, indices));
            addGameObjectToScene(go);
        }
    }

    @Override
    public void update(float dt)
    {
        float camSpeed = 40f;
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

        for (GameObject gameObject : gameObjects)
            gameObject.update(dt);

        for (GameObject gameObject : gameObjects) {
            float rotation = gameObject.getTransform().rotation.z + 0.5f;
            if (rotation > 360) {
                rotation = 0;
            }
            gameObject.getTransform().setRotation(rotation, rotation, rotation);
        }

        renderer.render(camera);
    }
}
