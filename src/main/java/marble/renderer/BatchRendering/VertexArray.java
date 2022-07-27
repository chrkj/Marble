package marble.renderer.BatchRendering;

import marble.renderer.IndexBuffer;
import marble.renderer.RenderingAPI;
import marble.renderer.VertexBuffer;

import java.util.List;

public abstract class VertexArray
{
    protected int id;

    public abstract void bind();
    public abstract void unbind();
    public abstract void cleanUp();
    public abstract void addVertexBuffer(VertexBuffer vertexBuffer);
    public abstract void setIndexBuffer(IndexBuffer indexBuffer);
    public abstract List<VertexBuffer> getVertexBuffers();
    public abstract IndexBuffer getIndexBuffer();

    static VertexArray create()
    {
        switch (RenderingAPI.getCurrent())
        {
            case None:      return null;
            case OPENGL:    return new OpenGLVertexArray();
        }
        return null;
    }
}
