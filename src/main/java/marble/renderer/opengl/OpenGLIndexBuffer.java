package marble.renderer.opengl;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

import marble.renderer.IndexBuffer;

public class OpenGLIndexBuffer extends IndexBuffer
{
    private final int ID;
    private final int count;

    public OpenGLIndexBuffer(int[] data, int count)
    {
        ID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        this.count = count;
    }

    @Override
    public void bind()
    {
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
    }

    @Override
    public void unbind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public int getCount()
    {
        return this.count;
    }

    @Override
    public void delete()
    {
        glDeleteBuffers(ID);
    }
}
