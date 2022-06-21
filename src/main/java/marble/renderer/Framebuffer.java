package marble.renderer;

import marble.editor.FramebufferSpecification;

public abstract class Framebuffer {

    public int textureId = 0;
    public int framebufferId = 0;
    public int depthbufferId = 0;
    public int redIntTextureId = 0;

    protected FramebufferSpecification specification;

    public static Framebuffer create(FramebufferSpecification specification)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None -> {
                return null;
            }
            case OPENGL -> {
                return new OpenGLFramebuffer(specification);
            }
        }
        return null;
    }

    public abstract void recreate();
    public abstract FramebufferSpecification getSpecification();
    public abstract int readPixel(float x, float y);
    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
}

