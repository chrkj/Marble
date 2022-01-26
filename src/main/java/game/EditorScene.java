package game;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import marble.Window;
import marble.scene.Scene;
import marble.util.Loader;
import marble.camera.Camera;
import marble.entity.Entity;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;
import marble.entity.components.light.LightType;
import marble.entity.components.light.LightFactory;

public class EditorScene extends Scene {

    @Override
    public void init()
    {
        mainCamera = new Camera(new Vector3f(20f, 0f, 26f));
        mainCamera.rotate(7f,-35f,-30f);
        ambientLight.set(0.1f);
        //{
        //    Entity entity = createEntity("Cube")
        //            .setPosition(-3,-2,3)
        //            .setAmbient(1,1,1,1)
        //            .setDiffuse(1,1,1,1)
        //            .setReflectance(0)
        //            .addTexture(Loader.loadTexture("assets/textures/grassblock.png"))
        //            .addComponent(Loader.loadMeshObj("assets/obj/cube.obj"));
        //    addEntityToScene(entity);
        //}
        {
            Entity entity = createEntity("Beer")
                    .setPosition(3,-2,3)
                    .addComponent(Loader.loadMeshObj("assets/obj/helm.obj"));
            addEntityToScene(entity);
        }
        {
            Entity entity = createEntity("DirLight")
                    .setPosition(-1,2,4)
                    .setRotation(333,53,0)
                    .addComponent(Loader.loadMeshObj("assets/obj/arrow.obj"))
                    .addComponent(LightFactory.getLight(LightType.DIRECTIONAL));
            addEntityToScene(entity);
        }
    }

    @Override
    public void update(float dt)
    {
        handleInput(dt);
    }

    private void handleInput(float dt)
    {
        float camSpeed = 100 * dt;
        float camRotSpeed = 15 * dt;
        // Movement
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            mainCamera.move(0,0, -camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            mainCamera.move(0,0, camSpeed);
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            mainCamera.move(-camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            mainCamera.move(camSpeed,0,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_E))
            mainCamera.move(0, -camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_Q))
            mainCamera.move(0, camSpeed,0);
        if (KeyListener.isKeyPressed(KeyEvent.VK_SPACE) && timeSinceSceneStarted() > 1)
            changeScene(new GameScene());
        if(KeyListener.isKeyPressed(KeyEvent.VK_1))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if(KeyListener.isKeyPressed(KeyEvent.VK_2))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (glfwGetInputMode(Window.windowPtr, GLFW_CURSOR) == GLFW_CURSOR_DISABLED)
            mainCamera.rotate(-MouseListener.mouseDelta().x * camRotSpeed, -MouseListener.mouseDelta().y * camRotSpeed, 0);
    }

}
