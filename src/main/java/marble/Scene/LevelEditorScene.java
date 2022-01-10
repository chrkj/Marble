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

import java.awt.event.KeyEvent;
import java.lang.reflect.Type;

public class LevelEditorScene extends Scene {

    private float camSpeed = 40f;

    public LevelEditorScene()
    {
        System.out.println("Inside level editor scene.");
    }

    @Override
    public void init()
    {
        camera = new Camera(new Vector3f(0,0,20));

        //int xOffset = 10;
        //int yOffset = 10;
        //float totalWidth = (float) (600 - xOffset * 2);
        //float totalHeight = (float) (300 - yOffset * 2);
        //float sizeX = totalWidth / 100f;
        //float sizeY = totalHeight / 100f;
//
        //for (int x = 0; x < 100; x++) {
        //    for (int y = 0; y < 100; y++) {
        //        float xPos = xOffset + (x * sizeX);
        //        float yPos = yOffset + (y * sizeY);
        //        GameObject gameObject = new GameObject("Obj " + x + ", " + y, new Transform(new Vector3f(xPos, yPos, 0), new Vector3f(sizeX, sizeY,0)));
        //        gameObject.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
        //        addGameObjectToScene(gameObject);
        //    }
        //}

        float[] positions = new float[] {
                // VO
                -0.5f,  0.5f,  0.5f,
                // V1
                -0.5f, -0.5f,  0.5f,
                // V2
                0.5f, -0.5f,  0.5f,
                // V3
                0.5f,  0.5f,  0.5f,
                // V4
                -0.5f,  0.5f, -0.5f,
                // V5
                0.5f,  0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,
        };
        int[] indices = new int[] {
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
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
        GameObject go = new GameObject("Obj1");
        go.getTransform().position = new Vector3f(0,0,-2);
        go.addComponent(new Mesh(positions, colors, indices));
        addGameObjectToScene(go);
    }

    @Override
    public void update(float dt)
    {
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.position.y -= dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.position.y += dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.position.x += dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.position.x -= dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            camera.position.z += dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            camera.position.z -= dt * camSpeed;

        for (GameObject gameObject : gameObjects) {
            gameObject.update(dt);
            float rotation = gameObject.getTransform().rotation.z + 0.5f;
            if (rotation > 360) {
                rotation = 0;
            }
            gameObject.getTransform().setRotation(new Vector3f(rotation, rotation, rotation));
        }

        renderer.render();
    }
}
