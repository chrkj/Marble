package marble.renderer;

public abstract class IndexBuffer {

    public static IndexBuffer create(int[] data)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None -> {
                return null;
            }
            case OPENGL -> {
                return new OpenGLIndexBuffer(data);
            }
        }
        return null;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
}
