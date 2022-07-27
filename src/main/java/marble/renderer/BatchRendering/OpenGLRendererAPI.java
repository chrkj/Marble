package marble.renderer.BatchRendering;

import org.joml.Vector4f;
import static org.lwjgl.opengl.GL11.*;

public class OpenGLRendererAPI extends RendererAPI
{
    public void init()
    {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_LINE_SMOOTH);
    }

    public void setViewport(int x, int y, int width, int height)
    {
        glViewport(x, y, width, height);
    }

    public void setClearColor(Vector4f color)
    {
        glClearColor(color.x, color.y, color.z, color.w);
    }

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void drawIndexed(VertexArray vertexArray, int indexCount)
    {
        vertexArray.bind();
        int count = vertexArray.getIndexBuffer().getCount();
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_INT, 0);
    }

    public void drawLines(VertexArray vertexArray, int vertexCount)
    {
        vertexArray.bind();
        glDrawArrays(GL_LINES, 0, vertexCount);
    }

    public void setLineWidth(float width)
    {
        glLineWidth(width);
    }
}
