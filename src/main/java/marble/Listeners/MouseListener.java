package marble.listeners;

import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {

    private static int timesRun = 0;
    private static double scrollX, scrollY;
    private static double xPos, yPos, lastY, lastX;
    private static boolean inWindow = false;
    private static boolean isDragging = false;
    private static boolean allowInput = false;

    private static final Vector2f rotationVec = new Vector2f();
    private static final boolean[] mouseButtonPressed = new boolean[3];

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

        // Making sure current / last pos is set before allowing input calculations
        if (timesRun < 2)
            timesRun++;
        else
            allowInput = true;
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

    public static float getX()
    {
        return (float) xPos;
    }

    public static float getY()
    {
        return (float) yPos;
    }

    public static float getDx()
    {
        return (float) (lastX - xPos);
    }

    public static float getDy()
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

    public static boolean getIsDragging()
    {
        return isDragging;
    }

    public static void input()
    {
        if (allowInput) {
            rotationVec.x = 0;
            rotationVec.y = 0;
            boolean rotateX = getDx() != 0;
            boolean rotateY = getDy() != 0;
            if (rotateX)
                rotationVec.y = getDx();
            if (rotateY)
                rotationVec.x = getDy();
            lastX = xPos;
            lastY = yPos;
        }
    }

    public static Vector2f getRotationVec()
    {
        return rotationVec;
    }
}
