package marble.entity.components;

import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

import marble.imgui.ImGuiLayer;
import marble.renderer.Buffer;

public class Mesh extends Component {

    private final int vaoId;
    private final int vertexCount;
    private final boolean enableSmoothShading;
    private final List<Integer> vertexBufferObjectIds = new ArrayList<>();

    public Mesh(float[] verticesArray, float[] textureArray, int[] indicesArray, float[] normalsArray, boolean enableSmoothShading)
    {
        this.vertexCount = indicesArray.length;
        this.enableSmoothShading = enableSmoothShading;

        // Create and bind VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO's
        vertexBufferObjectIds.add(Buffer.createVertexbuffer(verticesArray, 0, 3));
        vertexBufferObjectIds.add(Buffer.createVertexbuffer(textureArray, 1, 2));
        vertexBufferObjectIds.add(Buffer.createVertexbuffer(normalsArray, 2, 3));
        vertexBufferObjectIds.add(Buffer.createIndexbuffer(indicesArray));

        // Unbind VAO
        glBindVertexArray(0);
    }

    @Override
    public void render()
    {
        if (ImGuiLayer.polygonMode.get())
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Bind the texture
        if (entity.material.hasTexture() == 1)
            entity.material.getTexture().bind();

        // Draw the mesh
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        ImGuiLayer.drawCalls++;

        // Restore state
        if (entity.material.hasTexture() == 1)
            entity.material.getTexture().unbind();

        glBindVertexArray(0);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void cleanUp()
    {
        glDisableVertexAttribArray(0);

        // Delete VBOs
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        for (int vboId : vertexBufferObjectIds)
            glDeleteBuffers(vboId);

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }

}
