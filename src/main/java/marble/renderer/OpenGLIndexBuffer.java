package marble.renderer;

import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

public class OpenGLIndexBuffer extends IndexBuffer {

    private int ID = -1;

    public OpenGLIndexBuffer(int[] data)
    {
        ID = glGenBuffers();
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);
        buffer.put(data).flip();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
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
    public void delete()
    {
        glDeleteBuffers(ID);
    }
}
