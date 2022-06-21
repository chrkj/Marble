package marble.renderer;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class OpenGLVertexbuffer extends Vertexbuffer {

    private int ID = -1;

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
