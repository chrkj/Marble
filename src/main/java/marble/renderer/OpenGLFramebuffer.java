package marble.renderer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL32C.glFramebufferTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL45.glCreateFramebuffers;

import marble.editor.ConsolePanel;

public class OpenGLFramebuffer extends Framebuffer {

    public OpenGLFramebuffer(FramebufferSpecification specification)
    {
        this.specification = specification;
        create();
    }

    @Override
    public void recreate()
    {
        delete();
        create();
    }

    @Override
    public FramebufferSpecification getSpecification()
    {
        return specification;
    }

    @Override
    public int readPixel(float x, float y, int index)
    {
        bind();
        glReadBuffer(GL_COLOR_ATTACHMENT0 + index);
        int[] pixels = new int[1];
        glReadPixels((int) x, (int) y, 1, 1, GL_RED_INTEGER, GL_INT, pixels);
        unbind();
        return pixels[0];
    }

    @Override
    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
        glViewport(0, 0, specification.width, specification.height);
    }

    @Override
    public void unbind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    @Override
    public void delete()
    {
        glDeleteFramebuffers(framebufferID);
        for (int colorAttachmentID : colorAttachmentIDs)
            glDeleteTextures(colorAttachmentID);
        glDeleteRenderbuffers(depthAttachmentID);
        framebufferID = 0;
        depthAttachmentID = 0;
        colorAttachmentIDs.clear();
    }

    private void create()
    {
        framebufferID = glCreateFramebuffers();
        bind();

        createColorTextures(specification.textureFormats.size(), colorAttachmentIDs);
        for (int i = 0; i < specification.textureFormats.size(); i++)
        {
            glBindTexture(GL_TEXTURE_2D, colorAttachmentIDs.get(i));
            switch (specification.textureFormats.get(i))
            {
                case RGB8 ->        attachColorTexture(colorAttachmentIDs.get(i), GL_RGB, GL_RGB, i);
                case RED_INTEGER -> attachColorTexture(colorAttachmentIDs.get(i), GL_R32I, GL_RED_INTEGER, i);
            }
        }

        if (specification.depthFormat != TextureFormat.NONE)
            attachDepthTexture(GL_DEPTH24_STENCIL8, GL_DEPTH_STENCIL_ATTACHMENT);

        int[] drawBuffers = new int[colorAttachmentIDs.size()];
        for (int i = 0; i < colorAttachmentIDs.size(); i++)
            drawBuffers[i] = GL_COLOR_ATTACHMENT0 + i;
        glDrawBuffers(drawBuffers);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            ConsolePanel.log("glCheckFramebufferStatus failed");

        unbind();
    }

    private void attachColorTexture(int id, int format, int internalFormat, int index)
    {
        glTexImage2D(GL_TEXTURE_2D, 0, format, specification.width, specification.height, 0, internalFormat, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + index, id, 0);
    }

    private void attachDepthTexture(int format, int attachmentType)
    {
        depthAttachmentID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthAttachmentID);
        glTexStorage2D(GL_TEXTURE_2D, 1, format, specification.width, specification.height);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        glFramebufferTexture2D(GL_FRAMEBUFFER, attachmentType, GL_TEXTURE_2D, depthAttachmentID, 0);
    }

    private void createColorTextures(int size, List<Integer> data)
    {
        int[] ids = new int[size];
        glGenTextures(ids);
        Integer[] idsI = Arrays.stream(ids).boxed().toArray(Integer[]::new);
        Collections.addAll(colorAttachmentIDs, idsI);
    }

    public int getColorAttachmentRendererID()
    {
        return colorAttachmentIDs.get(0);
    }

    public int getColorAttachmentRendererID(int index)
    {
        return colorAttachmentIDs.get(index);
    }

}
