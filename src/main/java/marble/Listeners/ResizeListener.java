package marble.Listeners;

import marble.Window;
import marble.Camera.Camera;

import static org.lwjgl.opengl.GL11.glViewport;

public class ResizeListener {

    public static void windowResizeCallback(final long window, final int width, final int height)
    {
        Window.get().setWidth(width);
        Window.get().setHeight(height);
        glViewport(0, 0, width, height);
        Camera.adjustProjectionMatrix();
    }
}
