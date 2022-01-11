package Sandbox;

import Marble.Camera.Camera;
import Marble.GameObject.Components.Mesh;
import Marble.GameObject.Components.Texture;
import Marble.GameObject.GameObject;
import Marble.GameObject.Transform;
import Marble.Listeners.KeyListener;
import Marble.Scene.Scene;
import Marble.Window;
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
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
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

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};
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
            if (rotation > 360) {
                rotation = 0;
            }
            gameObject.transform.setRotation(rotation, rotation, rotation);
        }

        renderer.render(camera);
    }
}
