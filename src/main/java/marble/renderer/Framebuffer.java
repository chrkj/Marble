package marble.renderer;

import marble.renderer.opengl.OpenGLFramebuffer;

import java.util.List;
import java.util.ArrayList;

public abstract class Framebuffer {

    protected int framebufferID;
    protected int depthAttachmentID;
    protected FramebufferSpecification specification;
    protected List<Integer> colorAttachmentIDs = new ArrayList<>();

    public enum TextureFormat { NONE, RGB8, RED_INTEGER, DEPTH24_STENCIL8 }

    public static Framebuffer create(FramebufferSpecification specification)
    {
        switch (RenderingAPI.getCurrent())
        {
            case None ->   { return null; }
            case OPENGL -> { return new OpenGLFramebuffer(specification); }
        }
        return null;
    }

    private static boolean isDepthFormat(TextureFormat format)
    {
        switch (format)
        {
            case DEPTH24_STENCIL8:
                return true;
        }
        return false;
    }

    public abstract void bind();
    public abstract void unbind();
    public abstract void delete();
    public abstract void recreate();
    public abstract int readPixel(float x, float y, int index);
    public abstract FramebufferSpecification getSpecification();
    public abstract int getColorAttachmentRendererID();
    public abstract int getColorAttachmentRendererID(int index);

    public static final class FramebufferSpecification
    {
        public int width = 0;
        public int height = 0;

        private TextureFormat depthFormat = TextureFormat.NONE;
        private List<TextureFormat> textureFormats = new ArrayList<>();

        public FramebufferSpecification(TextureFormat... formats)
        {
            for (TextureFormat textureFormat : formats)
            {
                if (isDepthFormat(textureFormat))
                    depthFormat = textureFormat;
                else
                    textureFormats.add(textureFormat);
            }
        }

        public TextureFormat getDepthFormat()
        {
            return depthFormat;
        }

        public List<TextureFormat> getTextureFormats()
        {
            return textureFormats;
        }

    }

}

