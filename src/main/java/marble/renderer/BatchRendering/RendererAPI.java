package marble.renderer.BatchRendering;

import marble.renderer.RenderingAPI;

public class RendererAPI
{
    public static RendererAPI create()
    {
        switch (RenderingAPI.getCurrent())
        {
            case None:      return null;
            case OPENGL:    return new OpenGLRendererAPI();
        }
        return null;
    }
}
