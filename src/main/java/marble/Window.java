package Marble;

import Sandbox.GameScene;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import Marble.ImGui.ImGuiLayer;
import Marble.Listeners.KeyListener;
import Marble.Listeners.MouseListener;
import Marble.Listeners.ResizeListener;
import Marble.Scene.Scene;
import Sandbox.EditorScene;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWErrorCallback;

import Marble.util.Time;

import static java.sql.Types.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Window {
    public static long windowPtr;

    private String glslVersion = null;
    private static int width;
    private static int height;
    private final String title;
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private ImGuiLayer imguiLayer;

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

    public static Scene getCurrentScene()
    {
        return Window.currentScene;
    }

    public Window(String title, ImGuiLayer imGuiLayer)
    {
       width = 1280;
       height = 720;
       resized = false;
       this.title = title;
       this.imguiLayer = imGuiLayer;
    }

    public void run()
    {
        System.out.println("LWJGL Version: " + Version.getVersion() + "!");
        init();
        loop();
    }

    public void init()
    {
        initWindow();
        initImGui();
        imGuiGlfw.init(windowPtr, true);
        imGuiGl3.init(glslVersion);
    }

    private void initWindow()
    {
        // Setup an error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW.");

        //
        glslVersion = "#version 130";
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        windowPtr = glfwCreateWindow(width, height, this.title, NULL, NULL);
        if (windowPtr == NULL)
            throw new IllegalStateException("Failed to create the GLFW window.");

        // Set callback
        glfwSetCursorPosCallback(windowPtr, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(windowPtr, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(windowPtr, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(windowPtr, KeyListener::keyCallback);
        glfwSetWindowSizeCallback(windowPtr, ResizeListener::windowResizeCallback);

        // Make the OpenGl context current
        glfwMakeContextCurrent(windowPtr);
        GL.createCapabilities();

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowPtr);

        // Enable depth-buffer
        glEnable(GL_DEPTH_TEST);

        // Set initial scene
        Window.changeScene(0);
    }

    private void initImGui()
    {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    public void loop()
    {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        // Game loop
        while (!glfwWindowShouldClose(windowPtr)) {
            glClearColor(0.1f, 0.09f, 0.1f, 1.0f);

            update(dt);

            imGuiGlfw.newFrame();
            ImGui.newFrame();
            imguiLayer.createLayer();
            ImGui.render();
            imGuiGl3.renderDrawData(ImGui.getDrawData());

            if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                glfwMakeContextCurrent(backupWindowPtr);
            }

            glfwSwapBuffers(windowPtr);
            glfwPollEvents();

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void update(float dt)
    {
        if (dt >= 0)
            currentScene.update(dt);
            glfwSetWindowTitle(windowPtr, title + " - " + (int)(1/ dt) + " fps");
    }

    public void destroy()
    {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks(windowPtr);
        glfwDestroyWindow(windowPtr);
        glfwTerminate();
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
