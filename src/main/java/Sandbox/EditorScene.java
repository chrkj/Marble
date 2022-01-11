package Sandbox;

import Marble.Camera.Camera;
import Marble.GameObject.Components.Mesh;
import Marble.GameObject.Components.Texture;
import Marble.GameObject.GameObject;
import Marble.Listeners.KeyListener;
import Marble.GameObject.Transform;
import Marble.Scene.Scene;
import Marble.Window;
import imgui.ImGui;
import org.joml.Vector3f;

import java.awt.event.KeyEvent;

public class EditorScene extends Scene {

    float[][] xTrans;
    float[][] yTrans;
    float[][] zTrans;
    float[][] xRot;
    float[][] yRot;
    float[][] zRot;
    float[][] scale;

    public EditorScene()
    {
        System.out.println("Inside editor scene.");
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
            GameObject go = new GameObject("Cube1", new Transform(new Vector3f(), new Vector3f(30, 78, 10), 1));
            go.addComponent(new Mesh(positions, textCoords, indices, texture));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Cube2", new Transform(new Vector3f(2,2,2), new Vector3f(66, 5, 17), 1));
            go.addComponent(new Mesh(positions, textCoords, indices, texture));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Cube3", new Transform(new Vector3f(-2,-2,-2), new Vector3f(66, 5, 17), 1));
            go.addComponent(new Mesh(positions, textCoords, indices, texture));
            addGameObjectToScene(go);
        }

        xTrans = new float[gameObjects.size()][1];
        yTrans = new float[gameObjects.size()][1];
        zTrans = new float[gameObjects.size()][1];
        xRot   = new float[gameObjects.size()][1];
        yRot   = new float[gameObjects.size()][1];
        zRot   = new float[gameObjects.size()][1];
        scale  = new float[gameObjects.size()][1];
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
            Window.changeScene(1);

        for (GameObject gameObject : gameObjects)
            gameObject.update(dt);

        for (int i = 0; i < gameObjects.size(); i++) {
            ImGui.begin("Cubes");
            ImGui.text(gameObjects.get(i).name);
            ImGui.text("Translation");
            ImGui.sliderFloat("xT" + gameObjects.get(i).name, xTrans[i], -30, 30);
            ImGui.sliderFloat("yT" + gameObjects.get(i).name, yTrans[i], -30, 30);
            ImGui.sliderFloat("zT" + gameObjects.get(i).name, zTrans[i], -30, 30);
            ImGui.text("Rotation");
            ImGui.sliderFloat("xR" + gameObjects.get(i).name, xRot[i], 0, 360);
            ImGui.sliderFloat("yR" + gameObjects.get(i).name, yRot[i], 0, 360);
            ImGui.sliderFloat("zR" + gameObjects.get(i).name, zRot[i], 0, 360);
            ImGui.text("Scale");
            ImGui.sliderFloat("Scale" + gameObjects.get(i).name, scale[i], 1, 10);
            ImGui.end();

            gameObjects.get(i).transform = new Transform(new Vector3f(xTrans[i][0], yTrans[i][0], zTrans[i][0]), new Vector3f(xRot[i][0], yRot[i][0], zRot[i][0]), scale[i][0]);
        }






        renderer.render(camera);
    }
}
