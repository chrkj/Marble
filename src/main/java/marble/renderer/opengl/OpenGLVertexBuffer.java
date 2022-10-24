package marble.renderer.opengl;

import static org.lwjgl.opengl.GL20.*;

import marble.renderer.VertexBuffer;
import marble.renderer.BatchRendering.BufferLayout;

public class OpenGLVertexBuffer extends VertexBuffer
{
    private final int ID;
    private BufferLayout layout;

    public OpenGLVertexBuffer(float[] data, int index, int size, int type)
    {
        ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, ID);
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, false, 0, 0);
    }

    public OpenGLVertexBuffer(int size)
    {
        ID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, ID);
        glBufferData(GL_ARRAY_BUFFER, size, GL_DYNAMIC_DRAW);
    }

    @Override
    public void bind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, ID);
    }

    @Override
    public void unbind()
    {
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    @Override
    public void delete()
    {
        glDeleteBuffers(ID);
    }

    @Override
    public BufferLayout getLayout()
    {
        return layout;
    }

    @Override
    public void setLayout(BufferLayout layout)
    {
        this.layout = layout;
    }

    @Override
    public void setData(float[] data)
    {
        glBindBuffer(GL_ARRAY_BUFFER, ID);
        //glBufferSubData(GL_ARRAY_BUFFER, 0, data); wont work?
        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);
    }
}
