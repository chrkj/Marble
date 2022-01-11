package marble.listeners;

import marble.Window;

public class ResizeListener {

    public static void windowResizeCallback(final long window, final int width, final int height)
    {
        Window.setWidth(width);
        Window.setHeight(height);
        Window.setResized(true);
    }
}
