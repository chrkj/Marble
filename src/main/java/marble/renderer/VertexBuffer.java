package marble.renderer;

public abstract class VertexBuffer {

    public static VertexBuffer create(float[] data, int index, int size)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None -> {
                return null;
            }
            case OPENGL -> {
                return new OpenGLVertexBuffer(data, index, size);
            }
        }
        return null;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
}
