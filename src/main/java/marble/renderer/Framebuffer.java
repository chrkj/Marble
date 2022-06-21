package marble.renderer;

import java.awt.*;

public abstract class Framebuffer {

    public int textureId = 0;
    public int redIntTextureId = 0;
    public int frameBufferId = 0;
    public int depthbufferId = 0;

    public static class FramebufferSpecification {
        public int width = 0;
        public int height = 0;
    }

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

    public abstract FramebufferSpecification getSpecification();
    public abstract int readPixel(float x, float y);
    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
}

