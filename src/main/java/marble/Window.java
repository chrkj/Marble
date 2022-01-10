package marble;

import Sandbox.GameScene;
import marble.Listeners.KeyListener;
import marble.Listeners.MouseListener;
import marble.Listeners.ResizeListener;
import marble.Scene.Scene;
import Sandbox.EditorScene;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWErrorCallback;

import marble.util.Time;

import static java.sql.Types.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    private long glfwWindow;

    private static int width;
    private static int height;
    private final String title;

    private static boolean resized;
    private static Scene currentScene;

    public static void changeScene(int newScene)
    {
        switch (newScene) {
            case 0:
                currentScene = new EditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new GameScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
        }
    }

    public static Scene getScene()
    {
        return Window.currentScene;
    }

    public Window(String title)
    {
       width = 1280;
       height = 720;
       resized = false;
       this.title = title;
    }

    public void run()
    {
        System.out.println("LWJGL Version: " + Version.getVersion() + "!");
        init();
        loop();
    }

    public void init()
    {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        glfwWindow = glfwCreateWindow(width, height, this.title, NULL, NULL);
        if (glfwWindow == NULL)
            throw new IllegalStateException("Failed to create the GLFW window.");

        // Set callback
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(glfwWindow, ResizeListener::windowResizeCallback);

        // Make the OpenGl context current
        glfwMakeContextCurrent(glfwWindow);
        GL.createCapabilities();

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(glfwWindow);

        // Enable depth-buffer
        glEnable(GL_DEPTH_TEST);

        // Set initial scene
        Window.changeScene(0);
    }

    public void loop()
    {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        // Game loop
        while (!glfwWindowShouldClose(glfwWindow)) {
            // Poll events
            glfwPollEvents();

            if (dt >= 0) {
                currentScene.update(dt);
                glfwSetWindowTitle(glfwWindow, title + " - " + (int)(1/dt) + " fps");
            }
            glfwSwapBuffers(glfwWindow);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public static int getWidth()
    {
        return width;
    }

    public static int getHeight()
    {
        return height;
    }

    public static void setWidth(int w)
    {
        width = w;
    }

    public static void setHeight(int h)
    {
        height = h;
    }

    public static void setResized(boolean r)
    {
        resized = r;
    }

    public static boolean isResized()
    {
        return resized;
    }
}
