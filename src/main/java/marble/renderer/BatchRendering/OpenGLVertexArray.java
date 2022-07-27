package marble.renderer.BatchRendering;

import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.glVertexAttribDivisor;
import static org.lwjgl.opengl.GL45.glCreateVertexArrays;

import marble.renderer.IndexBuffer;
import marble.renderer.VertexBuffer;
import marble.renderer.BatchRendering.BufferElement.*;

public class OpenGLVertexArray extends VertexArray
{
    private IndexBuffer indexBuffer;
    private int vertexBufferIndex = 0;
    private final List<VertexBuffer> vertexBuffers = new ArrayList<>();

    public OpenGLVertexArray()
    {
        id = glCreateVertexArrays();
    }

    @Override
    public void bind()
    {
        glBindVertexArray(id);
    }

    @Override
    public void unbind()
    {
        glBindVertexArray(0);
    }

    @Override
    public void cleanUp()
    {
        glDeleteVertexArrays(id);
    }

    @Override
    public void addVertexBuffer(VertexBuffer vertexBuffer)
    {
        glBindVertexArray(id);
        vertexBuffer.bind();

        var layout = vertexBuffer.getLayout();
        for (BufferElement element : layout.getElements())
        {
            switch (element.type)
            {
                case Float, Float2, Float3, Float4:
                {
                    glEnableVertexAttribArray(vertexBufferIndex);
                    glVertexAttribPointer(vertexBufferIndex, element.getComponentCount(), ShaderDataTypeToOpenGLBaseType(element.type), element.normalized, layout.getStride(), element.offset);
                    vertexBufferIndex++;
                    break;
                }
                case Int, Int2, Int3, Int4, Bool:
                {
                    glEnableVertexAttribArray(vertexBufferIndex);
                    glVertexAttribIPointer(vertexBufferIndex, element.getComponentCount(), ShaderDataTypeToOpenGLBaseType(element.type), layout.getStride(), element.offset);
                    vertexBufferIndex++;
                    break;
                }
                case Mat3, Mat4:
                {
                    var count = element.getComponentCount();
                    for (var i = 0; i < count; i++)
                    {
                        glEnableVertexAttribArray(vertexBufferIndex);
                        glVertexAttribPointer(vertexBufferIndex, count, ShaderDataTypeToOpenGLBaseType(element.type), element.normalized, layout.getStride(), (element.offset + (long) Float.BYTES * count * i));
                        glVertexAttribDivisor(vertexBufferIndex, 1);
                        vertexBufferIndex++;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void setIndexBuffer(IndexBuffer indexBuffer)
    {
        glBindVertexArray(id);
        indexBuffer.bind();

        this.indexBuffer = indexBuffer;
    }

    @Override
    public List<VertexBuffer> getVertexBuffers()
    {
        return vertexBuffers;
    }

    @Override
    public IndexBuffer getIndexBuffer()
    {
        return indexBuffer;
    }

    private static int ShaderDataTypeToOpenGLBaseType(ShaderDataType type)
    {
        return switch (type) {
            case Float, Float2, Float3,
                    Float4, Mat3, Mat4  -> GL_FLOAT;
            case Int, Int2, Int3, Int4  -> GL_INT;
            case Bool                   -> GL_BOOL;
            default                     -> 0;
        };
    }
}
