package sandbox;

import java.awt.event.KeyEvent;

import imgui.ImGui;
import imgui.type.ImFloat;
import imgui.flag.ImGuiDataType;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import marble.Window;
import marble.scene.Scene;
import marble.util.Loader;
import marble.camera.Camera;
import marble.renderer.Shader;
import marble.gameobject.Transform;
import marble.gameobject.GameObject;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;

public class EditorScene extends Scene {

    private ImFloat[] xTrans;
    private ImFloat[] yTrans;
    private ImFloat[] zTrans;
    private ImFloat[] xRot;
    private ImFloat[] yRot;
    private ImFloat[] zRot;
    private ImFloat[] rotSpeed;
    private ImFloat[] xScale;
    private ImFloat[] yScale;
    private ImFloat[] zScale;

    public EditorScene()
    {
        System.out.println("Loading Editor scene...");
    }

    @Override
    public void init()
    {
        camera = new Camera(new Vector3f(0,0,10));
        {
            GameObject go = new GameObject("Cube", new Transform(new Vector3f(3,0,0), new Vector3f(30, 78, 10)));
            go.addComponent(Loader.loadMeshOBJ("assets/obj/cube.obj"));
            go.addComponent(Loader.loadTexture("assets/textures/grassblock.png"));
            addGameObjectToScene(go);
        }
        {
            GameObject go = new GameObject("Bunny", new Shader("assets/shaders/wobble.glsl"));
            go.addComponent(Loader.loadMeshOBJ("assets/obj/bunny.obj"));
            addGameObjectToScene(go);
        }

        xTrans = new ImFloat[gameObjects.size()];
        yTrans = new ImFloat[gameObjects.size()];
        zTrans = new ImFloat[gameObjects.size()];
        xRot = new ImFloat[gameObjects.size()];
        yRot = new ImFloat[gameObjects.size()];
        zRot = new ImFloat[gameObjects.size()];
        rotSpeed = new ImFloat[gameObjects.size()];
        xScale = new ImFloat[gameObjects.size()];
        yScale = new ImFloat[gameObjects.size()];
        zScale = new ImFloat[gameObjects.size()];

        for (int i = 0; i < gameObjects.size(); i++) {
            xTrans[i] = new ImFloat(gameObjects.get(i).transform.position.x);
            yTrans[i] = new ImFloat(gameObjects.get(i).transform.position.y);
            zTrans[i] = new ImFloat(gameObjects.get(i).transform.position.z);
            xRot[i] = new ImFloat(gameObjects.get(i).transform.rotation.x);
            yRot[i] = new ImFloat(gameObjects.get(i).transform.rotation.y);
            zRot[i] = new ImFloat(gameObjects.get(i).transform.rotation.z);
            rotSpeed[i] = new ImFloat(0);
            xScale[i] = new ImFloat(gameObjects.get(i).transform.scale.x);
            yScale[i] = new ImFloat(gameObjects.get(i).transform.scale.y);
            zScale[i] = new ImFloat(gameObjects.get(i).transform.scale.z);
        }

    }

    @Override
    public void update(float dt)
    {
        // Input
        handleInput(dt);

        // Update
        for (int i = 0; i < gameObjects.size(); i++) {
            createImguiLayer(i);
            gameObjects.get(i).update(dt);
        }

        // Render
        renderer.render(camera);
    }

    private void handleInput(float dt)
    {
        float camSpeed = 10 * dt;
        float camRotSpeed = 15 * dt;
        // Movement
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.move(0,0, -camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.move(0,0, camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.move(-camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.move(camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            camera.move(0, -camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            camera.move(0, camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            changeScene(new GameScene());
        if(KeyListener.isKeyPressed(KeyEvent.VK_1))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if(KeyListener.isKeyPressed(KeyEvent.VK_2))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (glfwGetInputMode(Window.windowPtr, GLFW_CURSOR) != GLFW_CURSOR_NORMAL)
            camera.rotate(-MouseListener.getRotationVec().x * camRotSpeed, -MouseListener.getRotationVec().y * camRotSpeed, 0);
        ImGui.begin("Game Objects");
        ImGui.text("Camera");
        ImGui.text(String.format("Position: %f %f %f", camera.getPosition().x, camera.getPosition().y, camera.getPosition().z));
        ImGui.text(String.format("Rotation: %f %f %f", camera.getRotation().x, camera.getRotation().y, camera.getRotation().z));
        ImGui.spacing();
        ImGui.spacing();
        ImGui.end();
    }

    private void createImguiLayer(int i)
    {
        ImGui.begin("Game Objects");
        ImGui.text(gameObjects.get(i).name);
        ImGui.text("Translation");
        ImGui.sliderScalar("Tx" + i, ImGuiDataType.Float, xTrans[i], -30, 30);
        ImGui.sliderScalar("Ty" + i, ImGuiDataType.Float, yTrans[i], -30, 30);
        ImGui.sliderScalar("Tz" + i, ImGuiDataType.Float, zTrans[i], -30, 30);
        ImGui.text("Rotation");
        ImGui.sliderScalar("Rx" + i, ImGuiDataType.Float, xRot[i], 0, 360);
        ImGui.sliderScalar("Ry" + i, ImGuiDataType.Float, yRot[i], 0, 360);
        ImGui.sliderScalar("Rz" + i, ImGuiDataType.Float, zRot[i], 0, 360);
        ImGui.sliderScalar("Speed" + i, ImGuiDataType.Float, rotSpeed[i], 0, 10);
        ImGui.text("Scale");
        ImGui.sliderScalar("xScale" + i, ImGuiDataType.Float, xScale[i], 0.1f, 10);
        ImGui.sliderScalar("yScale" + i, ImGuiDataType.Float, yScale[i], 0.1f, 10);
        ImGui.sliderScalar("zScale" + i, ImGuiDataType.Float, zScale[i], 0.1f, 10);
        ImGui.spacing();
        ImGui.spacing();
        ImGui.end();
        if (rotSpeed[i].get() == 0) {
            gameObjects.get(i).transform.setRotation(xRot[i].get(), yRot[i].get(), zRot[i].get());
        } else {
            float rotationX = gameObjects.get(i).transform.rotation.x + rotSpeed[i].get();
            float rotationY = gameObjects.get(i).transform.rotation.y + rotSpeed[i].get();
            float rotationZ = gameObjects.get(i).transform.rotation.z + rotSpeed[i].get();
            if (rotationX > 360)
                rotationX = 0;
            if (rotationY > 360)
                rotationY = 0;
            if (rotationZ > 360)
                rotationZ = 0;
            gameObjects.get(i).transform.setRotation(rotationX, rotationY, rotationZ);
            xRot[i].set(rotationX);
            yRot[i].set(rotationY);
            zRot[i].set(rotationZ);
        }
        gameObjects.get(i).transform.setScale(xScale[i].get(), yScale[i].get(), zScale[i].get());
        gameObjects.get(i).transform.setPosition(xTrans[i].get(), yTrans[i].get(), zTrans[i].get());
    }
}
