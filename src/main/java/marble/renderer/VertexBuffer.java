package marble.renderer;

import marble.renderer.BatchRendering.BufferLayout;
import marble.renderer.opengl.OpenGLVertexBuffer;

public abstract class VertexBuffer
{
    public static VertexBuffer create(float[] vertices)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None:      return null;
            case OPENGL:    return new OpenGLVertexBuffer(vertices);
        }
        return null;
    }

    public static VertexBuffer create(int size)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None:      return null;
            case OPENGL:    return new OpenGLVertexBuffer(size);
        }
        return null;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
    public abstract BufferLayout getLayout();
    public abstract void setLayout(BufferLayout layout);
    public abstract void setData(int[] data, int size); // TODO: Create custom data obj
}
