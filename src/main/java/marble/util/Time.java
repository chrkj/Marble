package marble.util;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Time
{
    public static final double timeStarted = glfwGetTime();

    public static float getTime()
    {
        return (float) (glfwGetTime() - timeStarted);
    }

}
