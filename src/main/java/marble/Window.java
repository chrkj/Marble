package marble;

import static java.sql.Types.*;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.flag.ImGuiConfigFlags;

import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import marble.util.Time;
import marble.scene.Scene;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;
import marble.listeners.ResizeListener;

import sandbox.EditorScene;

public class Window {

    public static long windowPtr;
    public static Scene nextScene;
    public static boolean shouldChangeScene;

    private final String title;
    private final String glslVersion = "#version 460";
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private static int width;
    private static int height;
    private static boolean resized;
    private static Scene currentScene;

    public static void changeScene(Scene newScene)
    {
        currentScene.cleanUp();
        currentScene = newScene;
        currentScene.init();
        currentScene.start();
        shouldChangeScene = false;
    }

    public static Scene getCurrentScene()
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
        loop();
    }

    public void init()
    {
        System.out.println("LWJGL Version: " + Version.getVersion() + "!");
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

        // OpenGL version
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create the window
        windowPtr = glfwCreateWindow(width, height, this.title, NULL, NULL);
        if (windowPtr == NULL)
            throw new IllegalStateException("Failed to create the GLFW window.");

        // Set callback
        glfwSetKeyCallback(windowPtr, KeyListener::keyCallback);
        glfwSetScrollCallback(windowPtr, MouseListener::mouseScrollCallback);
        glfwSetCursorPosCallback(windowPtr, MouseListener::mousePosCallback);
        glfwSetCursorEnterCallback(windowPtr, MouseListener::mouseEnterCallback);
        glfwSetMouseButtonCallback(windowPtr, MouseListener::mouseButtonCallback);
        glfwSetWindowSizeCallback(windowPtr, ResizeListener::windowResizeCallback);

        // Make the OpenGl context current
        glfwMakeContextCurrent(windowPtr);
        GL.createCapabilities();

        // Enable v-sync
        glfwSwapInterval(GLFW_TRUE);

        // Make the window visible
        glfwShowWindow(windowPtr);

        // Enable depth-buffer
        glEnable(GL_DEPTH_TEST);

        // Enable culling
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        // Set initial scene
        initScene(new EditorScene());
    }

    public void loop()
    {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        // Game loop
        while (!glfwWindowShouldClose(windowPtr)) {
            glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

            imGuiGlfw.newFrame();
            ImGui.newFrame();
            update(dt);
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

            if(shouldChangeScene)
                changeScene(nextScene);

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    public void destroy()
    {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        currentScene.cleanUp();
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

    private void initScene(Scene scene)
    {
        currentScene = scene;
        currentScene.init();
        currentScene.start();
    }

    private void initImGui()
    {
        ImGui.createContext();
        // ImGuiIO io = ImGui.getIO();
    }

    private void update(float dt)
    {
        if (dt >= 0)
            MouseListener.input();
        currentScene.updateScene(dt);
    }
}
