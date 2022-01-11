package marble.gameobject.components;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glDrawElements;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh extends Component {

    private final int posVboId;
    private final int idxVboId;
    //private final int colorVboId;
    private final int textureVboId;
    private final int vaoId;
    private final int vertexCount;
    private final Texture texture;

    private final List<Integer> vboIdList;

    public Mesh(float[] positions, float[] textureCords, int[] indices, Texture texture)
    {
        FloatBuffer positionBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        IntBuffer indiceBuffer = null;
        try {
            vertexCount = indices.length;
            vboIdList = new ArrayList<>();
            this.texture = texture;

            // Create VAO
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            posVboId = glGenBuffers();
            vboIdList.add(posVboId);
            positionBuffer = MemoryUtil.memAllocFloat(positions.length);
            positionBuffer.put(positions).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            //// Color VBO
            //colorVboId = glGenBuffers();
            //colorBuffer = MemoryUtil.memAllocFloat(colours.length);
            //colorBuffer.put(colours).flip();
            //glBindBuffer(GL_ARRAY_BUFFER, colorVboId);
            //glBufferData(GL_ARRAY_BUFFER, colorBuffer, GL_STATIC_DRAW);
            //glEnableVertexAttribArray(1);
            //glVertexAttribPointer(1, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            textureVboId = glGenBuffers();
            vboIdList.add(textureVboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textureCords.length);
            textCoordsBuffer.put(textureCords).flip();
            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Index VBO
            idxVboId = glGenBuffers();
            vboIdList.add(idxVboId);
            indiceBuffer = MemoryUtil.memAllocInt(indices.length);
            indiceBuffer.put(indices).flip();
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, idxVboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiceBuffer, GL_STATIC_DRAW);

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        } finally {
            if (positionBuffer != null)
                MemoryUtil.memFree(positionBuffer);
            if (textCoordsBuffer != null)
                MemoryUtil.memFree(textCoordsBuffer);
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
        // Activate firs texture bank
        glActiveTexture(GL_TEXTURE0);
        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, texture.getId());

        // Draw the mesh
        glBindVertexArray(getVaoId());

        glDrawElements(GL_TRIANGLES, getVertexCount(), GL_UNSIGNED_INT, 0);

        // Restore state
        glBindVertexArray(0);
    }

    public void cleanUp()
    {
        glDisableVertexAttribArray(0);

        // Delete VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vboIdList)
            glDeleteBuffers(vboId);

        texture.cleanUp();

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
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
