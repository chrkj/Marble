package marble.listeners;

import marble.Application;

public class ResizeListener
{
    public static void windowResizeCallback(final long window, final int width, final int height)
    {
        Application.setWidth(width);
        Application.setHeight(height);
    }
}
