package marble.renderer.BatchRendering;

import marble.renderer.RenderingAPI;
import org.joml.Vector4f;

public abstract class RendererAPI
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

    public abstract void init();
    public abstract void setViewport(int x, int y, int width, int height);
    public abstract void setClearColor(Vector4f color);
    public abstract void drawIndexed(VertexArray vertexArray, int indexCount);
    public abstract void drawLines(VertexArray vertexArray, int vertexCount);
    public abstract void setLineWidth(float width);
    public abstract void clear();
}
