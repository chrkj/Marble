package game;

import java.awt.event.KeyEvent;

import static org.lwjgl.glfw.GLFW.*;

import marble.Window;
import marble.scene.Scene;
import marble.camera.Camera;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;

public class GameScene extends Scene {

    @Override
    public void init()
    {
        mainCamera = new Camera();
    }

    @Override
    public void update(float dt)
    {
        handleInput(dt);
    }

    private void handleInput(float dt)
    {
        float camSpeed = 10 * dt;
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
            changeScene(new EditorScene());
        if(KeyListener.isKeyPressed(KeyEvent.VK_1))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        if(KeyListener.isKeyPressed(KeyEvent.VK_2))
            glfwSetInputMode(Window.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        if (glfwGetInputMode(Window.windowPtr, GLFW_CURSOR) != GLFW_CURSOR_NORMAL)
            mainCamera.rotate(-MouseListener.mouseDelta().x * camRotSpeed, -MouseListener.mouseDelta().y * camRotSpeed, 0);
    }
}
