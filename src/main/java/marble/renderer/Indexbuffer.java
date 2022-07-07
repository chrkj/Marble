package marble.renderer;

import marble.renderer.opengl.OpenGLIndexbuffer;

public abstract class Indexbuffer {

    public static Indexbuffer create(int[] data)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None -> {
                return null;
            }
            case OPENGL -> {
                return new OpenGLIndexbuffer(data);
            }
        }
        return null;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
}
