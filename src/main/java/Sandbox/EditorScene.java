package sandbox;

import marble.camera.Camera;
import marble.gameobject.components.Mesh;
import marble.gameobject.components.Texture;
import marble.gameobject.GameObject;
import marble.listeners.KeyListener;
import marble.gameobject.Transform;
import marble.listeners.MouseListener;
import marble.scene.Scene;
import marble.Window;
import imgui.ImGui;
import org.joml.Vector3f;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import static org.lwjgl.glfw.GLFW.*;

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
        camera = new Camera(new Vector3f(0,1,5));

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

        Arrays.fill(xTrans[0], 1f);
        Arrays.fill(yTrans[0], 1f);
        Arrays.fill(zTrans[0], 1f);
        Arrays.fill(xRot[0]  , 1f);
        Arrays.fill(yRot[0]  , 1f);
        Arrays.fill(zRot[0]  , 1f);
        Arrays.fill(scale[0] , 1f);
        glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }

    @Override
    public void update(float dt) throws Exception
    {
        float camSpeed = 10;
        float camRotSpeed = 15;
        // Movement
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.move(0,0, -camSpeed, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.move(0,0, camSpeed, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.move(-camSpeed,0,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.move(camSpeed,0,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            camera.move(0, -camSpeed,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            camera.move(0, camSpeed,0, dt);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            Window.changeScene(1);
        if(KeyListener.isKeyPressed(KeyEvent.VK_1))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if(KeyListener.isKeyPressed(KeyEvent.VK_2))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (glfwGetInputMode(Window.windowPtr, GLFW_CURSOR) != GLFW_CURSOR_NORMAL)
            camera.rotate(-MouseListener.getRotationVec().x * camRotSpeed, -MouseListener.getRotationVec().y * camRotSpeed, 0, dt);

        // Updating
        for (int i = 0; i < gameObjects.size(); i++) {
            createImguiLayer(i);
            gameObjects.get(i).update(dt);
        }

        // Rendering
        renderer.render(camera);
    }

    private void createImguiLayer(int i)
    {
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
}
