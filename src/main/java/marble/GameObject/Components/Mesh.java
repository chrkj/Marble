package marble.gameobject.components;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

import marble.imgui.ImGuiLayer;

public class Mesh extends Component {

    private final int vaoId;
    private final int vertexCount;
    private final boolean enableSmoothShading;
    private final List<Integer> vbos = new ArrayList<>();

    public Mesh(float[] verticesArray, float[] textureArray, int[] indicesArray, float[] normals, boolean enableSmoothShading)
    {
        this.vertexCount = indicesArray.length;
        this.enableSmoothShading = enableSmoothShading;
        FloatBuffer positionBuffer = null;
        FloatBuffer textCoordsBuffer = null;
        FloatBuffer vecNormalsBuffer = null;
        IntBuffer indiceBuffer = null;
        try {
            // Create VAO
            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Position VBO
            int posVboId = glGenBuffers();
            vbos.add(posVboId);
            positionBuffer = MemoryUtil.memAllocFloat(verticesArray.length);
            positionBuffer.put(verticesArray).flip();
            glBindBuffer(GL_ARRAY_BUFFER, posVboId);
            glBufferData(GL_ARRAY_BUFFER, positionBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(0);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

            // Texture coordinates VBO
            int textureVboId = glGenBuffers();
            vbos.add(textureVboId);
            textCoordsBuffer = MemoryUtil.memAllocFloat(textureArray.length);
            textCoordsBuffer.put(textureArray).flip();
            glBindBuffer(GL_ARRAY_BUFFER, textureVboId);
            glBufferData(GL_ARRAY_BUFFER, textCoordsBuffer, GL_STATIC_DRAW);
            glEnableVertexAttribArray(1);
            glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

            // Vertex normals VBO
            int normVboId = glGenBuffers();
            vbos.add(normVboId);
            vecNormalsBuffer = MemoryUtil.memAllocFloat(normals.length);
            vecNormalsBuffer.put(normals).flip();
            glBindBuffer(GL_ARRAY_BUFFER, normVboId);
            glBufferData(GL_ARRAY_BUFFER, vecNormalsBuffer, GL_STATIC_DRAW);
            glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

            // Index VBO
            int idxVboId = glGenBuffers();
            vbos.add(idxVboId);
            indiceBuffer = MemoryUtil.memAllocInt(indicesArray.length);
            indiceBuffer.put(indicesArray).flip();
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

    @Override
    public void start()
    {
    }

    @Override
    public void update(float dt)
    {
    }

    @Override
    public void render()
    {
        if (ImGuiLayer.polygonMode.get())
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Bind the texture
        // TODO: Fix texture bindings (slow)
        if (gameObject.hasComponent(Texture.class))
            gameObject.getComponent(Texture.class).bind();

        // Draw the mesh
        glBindVertexArray(vaoId);

        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);

        // Restore state
        glBindVertexArray(0);
        if (gameObject.hasComponent(Texture.class))
            gameObject.getComponent(Texture.class).unbind();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void cleanUp()
    {
        glDisableVertexAttribArray(0);

        // Delete VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vbos)
            glDeleteBuffers(vboId);

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

}
