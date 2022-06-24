package marble.renderer;

import marble.renderer.opengl.OpenGLVertexbuffer;

public abstract class Vertexbuffer {

    public static Vertexbuffer create(float[] data, int index, int size, int type)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None -> {
                return null;
            }
            case OPENGL -> {
                return new OpenGLVertexbuffer(data, index, size, type);
            }
        }
        return null;
    }

    public static Vertexbuffer create(int[] data, int index, int size, int type)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None -> {
                return null;
            }
            case OPENGL -> {
                return new OpenGLVertexbuffer(data, index, size, type);
            }
        }
        return null;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
}
