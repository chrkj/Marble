package marble.renderer;

import marble.renderer.opengl.OpenGLIndexBuffer;

public abstract class IndexBuffer
{
    public static IndexBuffer create(int[] indices, int size)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None:      return null;
            case OPENGL:    return new OpenGLIndexBuffer(indices, size);
        }
        return null;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract int getCount();
    public abstract void delete();
}
