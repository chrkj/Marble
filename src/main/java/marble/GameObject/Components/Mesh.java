package Marble.GameObject.Components;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Mesh extends Component {

    private final int vaoId;
    private final int vertexCount;

    public Mesh(float[] positions, float[] colours, int[] indices)
    {
        FloatBuffer positionBuffer = null;
        FloatBuffer colorBuffer = null;
        IntBuffer indiceBuffer = null;
        try {
            vertexCount = indices.length;

            // Create VAO
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int posVboId = glGenBuffers();
            positionBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Colour VBO
            int colourVboId = glGenBuffers();
            colorBuffer = MemoryUtil.memAllocFloat(colours.length);
            colorBuffer.put(colours).flip();
            glBindBuffer(GL_ARRAY_BUFFER, colourVboId);
            glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            int idxVboId = glGenBuffers();
            indiceBuffer = MemoryUtil.memAllocInt(indices.length);
            indiceBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (positionBuffer != null)
                MemoryUtil.memFree(positionBuffer);
            if (colorBuffer != null)
                MemoryUtil.memFree(colorBuffer);
            if (indiceBuffer != null)
                MemoryUtil.memFree(indiceBuffer);
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void render()
    {
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);
        glBindVertexArray(0);
    }

    @Override
    public void start()
    {
    }

    @Override
    public void update(float dt)
    {
    }
}
