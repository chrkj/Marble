package marble.renderer.opengl;

import java.nio.IntBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.*;

import marble.renderer.Vertexbuffer;

public class OpenGLVertexbuffer extends Vertexbuffer {

    private final int ID;

    public OpenGLVertexbuffer(float[] data, int index, int size, int type)
    {
        ID = glGenBuffers();
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);
        buffer.put(data).flip();
        glBindBuffer(GL_ARRAY_BUFFER, ID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, false, 0, 0);
        MemoryUtil.memFree(buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    public OpenGLVertexbuffer(int[] data, int index, int size, int type)
    {
        ID = glGenBuffers();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        glBindBuffer(GL_ARRAY_BUFFER, ID);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, type, false, 0, 0);
        MemoryUtil.memFree(buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
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
}
