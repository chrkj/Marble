package marble.listeners;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import marble.imgui.ImGuiLayer;

import java.awt.event.MouseEvent;

public class MouseListener {

    private static double scrollX, scrollY;
    private static double xPos, yPos, lastY, lastX;
    private static boolean inWindow = false;
    private static boolean isDragging = false;
    private static final boolean[] mouseButtonPressed = new boolean[3];

    private static final Vector2f delta = new Vector2f();

    private MouseListener()
    {
        scrollX = 0.0;
        scrollY = 0.0;
        xPos = 0.0;
        yPos = 0.0;
        lastX = 0.0;
        lastY = 0.0;
    }

    public static void mousePosCallback(long window, double xPosition, double yPosition)
    {
        lastX = xPos;
        lastY = yPos;
        xPos = xPosition;
        yPos = yPosition;
        isDragging = mouseButtonPressed[0] || mouseButtonPressed[1] || mouseButtonPressed[2];
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods)
    {
        if (action == GLFW_PRESS) {
            if (button < mouseButtonPressed.length)
                mouseButtonPressed[button] = true;
        } else if (action == GLFW_RELEASE) {
            if (button < mouseButtonPressed.length) {
                mouseButtonPressed[button] = false;
                isDragging = false;
            }
        }
    }

    public static void mouseEnterCallback(long window, boolean entered)
    {
        inWindow = entered;
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset)
    {
        scrollX = xOffset;
        scrollY = yOffset;
    }

    public static void endFrame()
    {
        scrollX = 0;
        scrollY = 0;
        lastX = xPos;
        lastY = yPos;
    }

    public static boolean mouseButtonDown(long window, int button, int action, int mods)
    {
        if (button < mouseButtonPressed.length)
            return mouseButtonPressed[button];
        else
            return false;
    }

    public static Vector2f mousePosition()
    {
        return new Vector2f((float) xPos - ImGuiLayer.getCursorScreenPos.x, (float) yPos - ImGuiLayer.getCursorScreenPos.y);
    }

    public static boolean isMousePressed(int keyCode)
    {
        return mouseButtonPressed[keyCode];
    }

    public static float getX()
    {
        return (float) xPos;
    }

    public static float getY()
    {
        return (float) yPos;
    }

    private static float getDx()
    {
        return (float) (lastX - xPos);
    }

    private static float getDy()
    {
        return (float) (lastY - yPos);
    }

    public static float getScrollX()
    {
        return (float) scrollX;
    }

    public static float getScrollY()
    {
        return (float) scrollY;
    }

    public static boolean isDragging()
    {
        return isDragging;
    }

    public static void calcDelta()
    {
        delta.x = 0;
        delta.y = 0;
        boolean rotateX = getDx() != 0;
        boolean rotateY = getDy() != 0;
        if (rotateX)
            delta.y = getDx();
        if (rotateY)
            delta.x = getDy();
        lastX = xPos;
        lastY = yPos;
    }

    public static Vector2f mouseDelta()
    {
        return delta;
    }

    public static boolean isInWindow()
    {
        return inWindow;
    }
}
