package marble.renderer;

import marble.editor.ConsolePanel;
import marble.editor.FramebufferSpecification;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL32C.glFramebufferTexture;
import static org.lwjgl.opengl.GL45.glCreateFramebuffers;


public class OpenGLFramebuffer extends Framebuffer {

    public OpenGLFramebuffer(FramebufferSpecification specification)
    {
        this.specification = specification;

        // Creating and binding the framebuffer
        framebufferId = glCreateFramebuffers();
        bind();

        ///////////////////
        /// RGB TEXTURE ///
        ///////////////////

        // "Bind" the newly created texture : all future texture functions will modify this texture
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Give an empty image to OpenGL ( the last "0" )
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, specification.width, specification.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        // Poor filtering. Needed !
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // Set "renderedTexture" as our colour attachment #0
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureId, 0);

        //////////////////////
        /// REDINT TEXTURE ///
        //////////////////////

        // "Bind" the newly created texture : all future texture functions will modify this texture
        redIntTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, redIntTextureId);

        // Give an empty image to OpenGL ( the last "0" )
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R32I, specification.width, specification.height, 0, GL_RED_INTEGER , GL_UNSIGNED_BYTE, 0);

        // Poor filtering. Needed !
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // Set "renderedTexture" as our colour attachment #0
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, redIntTextureId, 0);

        ////////////////////
        /// DEPTH BUFFER ///
        ////////////////////

        depthbufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthbufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, specification.width, specification.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbufferId);

        // Set the list of draw buffers.
        int[] DrawBuffers = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
        glDrawBuffers(DrawBuffers);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            ConsolePanel.log("glCheckFramebufferStatus failed");

        unbind();
    }

    @Override
    public void recreate()
    {
        delete();
        framebufferId = glCreateFramebuffers();
        bind();

        ///////////////////
        /// RGB TEXTURE ///
        ///////////////////

        // "Bind" the newly created texture : all future texture functions will modify this texture
        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);

        // Give an empty image to OpenGL ( the last "0" )
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, specification.width, specification.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);

        // Poor filtering. Needed !
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // Set "renderedTexture" as our colour attachment #0
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureId, 0);

        //////////////////////
        /// REDINT TEXTURE ///
        //////////////////////

        // "Bind" the newly created texture : all future texture functions will modify this texture
        redIntTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, redIntTextureId);

        // Give an empty image to OpenGL ( the last "0" )
        glTexImage2D(GL_TEXTURE_2D, 0, GL_R32I, specification.width, specification.height, 0, GL_RED_INTEGER , GL_UNSIGNED_BYTE, 0);

        // Poor filtering. Needed !
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        // Set "renderedTexture" as our colour attachment #0
        glFramebufferTexture(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, redIntTextureId, 0);

        ////////////////////
        /// DEPTH BUFFER ///
        ////////////////////

        depthbufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, depthbufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, specification.width, specification.height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthbufferId);

        // Set the list of draw buffers.
        int[] DrawBuffers = new int[]{ GL_COLOR_ATTACHMENT0, GL_COLOR_ATTACHMENT1 };
        glDrawBuffers(DrawBuffers);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            ConsolePanel.log("glCheckFramebufferStatus failed");

        unbind();
    }

    @Override
    public FramebufferSpecification getSpecification()
    {
        return specification;
    }

    @Override
    public int readPixel(float x, float y)
    {
        bind();

        glReadBuffer(GL_COLOR_ATTACHMENT1);
        int[] pixels = new int[1];
        glReadPixels((int) x, (int) y, 1, 1, GL_RED_INTEGER, GL_INT, pixels);

        unbind();

        return pixels[0];
    }

    @Override
    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferId);
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
        glDeleteFramebuffers(framebufferId);
        glDeleteTextures(textureId);
        glDeleteTextures(redIntTextureId);
        glDeleteRenderbuffers(depthbufferId);
    }
}
