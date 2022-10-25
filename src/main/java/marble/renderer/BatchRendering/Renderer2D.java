package marble.renderer.BatchRendering;

import marble.entity.components.RigidBody;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11C.glClear;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_BUFFER_BIT;

import marble.gui.MarbleGui;
import marble.renderer.Shader;
import marble.entity.Transform;
import marble.renderer.VertexBuffer;
import marble.entity.components.camera.Camera;

public class Renderer2D
{
    private static class LineVertex
    {
        public static int getSize()
        {
            return 3 * Float.BYTES + 4 * Float.BYTES;
        }
    }

    private static Shader lineShader;
    private static RendererAPI rendererAPI;
    private static VertexArray lineVertexArray;
    private static VertexBuffer lineVertexBuffer;
    private static float[] lineVertexData;
    private static int maxVertices = 8000;
    private static int lineWidth = 2;
    private static int lineVertexCount = 0;
    private static int lineVertexBufferPtr = 0;

    public static void init()
    {
        rendererAPI = RendererAPI.create();
        rendererAPI.init();

        lineShader = new Shader("assets/shaders/lineShader.glsl");
        lineShader.compile();

        lineVertexArray = VertexArray.create();
        lineVertexBuffer = VertexBuffer.create(maxVertices * LineVertex.getSize());
        var layout = new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Float3, "a_Position", false),
                new BufferElement(BufferElement.ShaderDataType.Float4, "a_Color", false)
        );
        lineVertexBuffer.setLayout(layout);
        lineVertexArray.addVertexBuffer(lineVertexBuffer);
        lineVertexData = new float[maxVertices * LineVertex.getSize()];
    }

    public static void startBatch()
    {
        lineVertexCount = 0;
        lineVertexBufferPtr = 0;
    }

    public static void beginScene(Camera camera)
    {
        lineShader.bind();
        lineShader.setUniformMat4("uProjection", camera.getProjectionMatrixEditor());
        lineShader.setUniformMat4("uView", camera.getViewMatrix());
        lineShader.unbind();
        startBatch();
    }

    public static void endScene()
    {
        flush();
    }

    public static void flush()
    {
        if (lineVertexCount > 0)
        {
            // forces subsequent draws in front
            glClear(GL_DEPTH_BUFFER_BIT);
            lineVertexBuffer.setData(lineVertexData);

            lineShader.bind();
            rendererAPI.setLineWidth(lineWidth);
            rendererAPI.drawLines(lineVertexArray, lineVertexCount);
            lineShader.unbind();
            MarbleGui.drawCalls++;
        }
    }

    public static void drawLine(Vector4f p0, Vector4f p1, Vector4f color)
    {
        lineVertexData[lineVertexBufferPtr++] = p0.x;
        lineVertexData[lineVertexBufferPtr++] = p0.y;
        lineVertexData[lineVertexBufferPtr++] = p0.z;
        lineVertexData[lineVertexBufferPtr++] = color.x;
        lineVertexData[lineVertexBufferPtr++] = color.y;
        lineVertexData[lineVertexBufferPtr++] = color.z;
        lineVertexData[lineVertexBufferPtr++] = color.w;

        lineVertexData[lineVertexBufferPtr++] = p1.x;
        lineVertexData[lineVertexBufferPtr++] = p1.y;
        lineVertexData[lineVertexBufferPtr++] = p1.z;
        lineVertexData[lineVertexBufferPtr++] = color.x;
        lineVertexData[lineVertexBufferPtr++] = color.y;
        lineVertexData[lineVertexBufferPtr++] = color.z;
        lineVertexData[lineVertexBufferPtr++] = color.w;

        lineVertexCount += 2;
    }

    public static void drawRect(RigidBody rb, Vector4f color)
    {
        var world = rb.getEntity().getWorldMatrix();
        var e0 = new Vector4f(- 1, - 1, - 1, 1).mul(world);
        var e1 = new Vector4f(+ 1, - 1, - 1, 1).mul(world);
        var e2 = new Vector4f(+ 1, - 1, + 1, 1).mul(world);
        var e3 = new Vector4f(- 1, - 1, + 1, 1).mul(world);
        var e4 = new Vector4f(- 1, + 1, - 1, 1).mul(world);
        var e5 = new Vector4f(+ 1, + 1, - 1, 1).mul(world);
        var e6 = new Vector4f(+ 1, + 1, + 1, 1).mul(world);
        var e7 = new Vector4f(- 1, + 1, + 1, 1).mul(world);

        drawLine(e0, e1, color);
        drawLine(e1, e2, color);
        drawLine(e2, e3, color);
        drawLine(e3, e0, color);

        drawLine(e0, e4, color);
        drawLine(e1, e5, color);
        drawLine(e2, e6, color);
        drawLine(e3, e7, color);

        drawLine(e4, e5, color);
        drawLine(e5, e6, color);
        drawLine(e6, e7, color);
        drawLine(e7, e4, color);
    }

}
