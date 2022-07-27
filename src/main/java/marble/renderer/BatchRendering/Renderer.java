package marble.renderer.BatchRendering;

import marble.renderer.VertexBuffer;

public class Renderer
{
    private int maxVertices = 80000;
    private VertexArray meshVertexArray;
    private VertexBuffer meshVertexBuffer;

    public void init()
    {
        meshVertexArray = VertexArray.create();
        meshVertexBuffer = VertexBuffer.create(maxVertices * 10); // Change 10 to size of meshVertex data
        var layout = new BufferLayout(
                new BufferElement(BufferElement.ShaderDataType.Int, "uEntityID", false),
                new BufferElement(BufferElement.ShaderDataType.Float, "uTime", false),
                new BufferElement(BufferElement.ShaderDataType.Int, "uTextureSampler", false),
                new BufferElement(BufferElement.ShaderDataType.Float3, "uAmbientLight", false),
                new BufferElement(BufferElement.ShaderDataType.Float, "uSpecularPower", false),
                new BufferElement(BufferElement.ShaderDataType.Mat4, "uView", false),
                new BufferElement(BufferElement.ShaderDataType.Mat4, "uWorld", false)
        );
        meshVertexBuffer.setLayout(layout);
    }
}
