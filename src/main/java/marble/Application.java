package marble;

import static java.sql.Types.*;

import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.flag.ImGuiConfigFlags;

import marble.editor.EditorLayer;
import marble.gui.MarbleGui;
import org.lwjgl.opengl.GL;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFWErrorCallback;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import marble.util.Time;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;
import marble.listeners.ResizeListener;

public class Application {

    public static long windowPtr;

    private EditorLayer editorLayer;
    private final String title;
    private final String glslVersion = "#version 460";
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private static int width;
    private static int height;
    private static boolean resized;

    public Application(String title)
    {
       width = 1920;
       height = 1080;
       resized = false;
       this.title = title;
    }

    public void init()
    {
        initWindow();
        initImGui();
        editorLayer = new EditorLayer();
        imGuiGlfw.init(windowPtr, true);
        imGuiGl3.init(glslVersion);
    }

    public void run()
    {
        float beginTime = Time.getTime();
        float endTime;
        float dt = -1.0f;

        // Game loop
        while (!glfwWindowShouldClose(windowPtr)) {
            startFrame();
            update(dt);
            endFrame();

            endTime = Time.getTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }
    }

    private void update(float dt)
    {
        if (dt >= 0)
            editorLayer.onUpdate(dt);
    }

    private void startFrame()
    {
        glfwPollEvents();
        imGuiGlfw.newFrame();
        ImGui.newFrame();
        ImGuizmo.beginFrame();
        MouseListener.calcDelta();
    }

    private void endFrame()
    {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        ImGui.render();
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        glfwSwapBuffers(windowPtr);
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
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        //glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

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

    private void initImGui()
    {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);
        MarbleGui.setupTheme();
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

}
