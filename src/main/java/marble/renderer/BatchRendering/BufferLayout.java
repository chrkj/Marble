package marble.renderer.BatchRendering;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class BufferLayout
{
    private int stride;
    private final ArrayList<BufferElement> bufferElements = new ArrayList<>();

    public BufferLayout(BufferElement... bufferElements)
    {
        this.bufferElements.addAll(Arrays.asList(bufferElements));
    }

    private void calculateOffsetsAndStride()
    {
        int offset = 0;
        stride = 0;
        for (var element : bufferElements)
        {
            element.offset = offset;
            offset += element.size;
            stride += element.size;
        }
    }

    public int getStride()
    {
        return stride;
    }

    public List<BufferElement> getElements()
    {
        return bufferElements;
    }
}
