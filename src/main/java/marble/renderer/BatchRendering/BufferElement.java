package marble.renderer.BatchRendering;

public class BufferElement
{
    public int size;
    public int offset;
    public final boolean normalized;
    public final ShaderDataType type;

    private final String name;

    public enum ShaderDataType { None, Float, Float2, Float3, Float4, Mat3, Mat4, Int, Int2, Int3, Int4, Bool };

    public BufferElement(ShaderDataType type, String name, boolean normalized)
    {
        this.type = type;
        this.name = name;
        this.normalized = normalized;
    }

    public int getComponentCount()
    {
        return switch (type) {
            case Int, Bool, Float   -> 1;
            case Int2, Float2       -> 2;
            case Int3, Mat3, Float3 -> 3;
            case Int4, Mat4, Float4 -> 4;
            default -> 0;
        };
    }
}
