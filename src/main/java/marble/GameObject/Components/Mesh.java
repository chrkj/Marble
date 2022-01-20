package marble.gameobject.components;

import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

import marble.gameobject.components.light.DirectionalLight;
import marble.imgui.ImGuiLayer;
import marble.renderer.VertexBuffer;

public class Mesh extends Component {

    private final int vaoId;
    private final int vertexCount;
    private final boolean enableSmoothShading;
    private final List<Integer> vbos = new ArrayList<>();

    public Mesh(float[] verticesArray, float[] textureArray, int[] indicesArray, float[] normalsArray, boolean enableSmoothShading)
    {
        this.vertexCount = indicesArray.length;
        this.enableSmoothShading = enableSmoothShading;

        // Create and bind VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO's
        vbos.add(VertexBuffer.createVbo(verticesArray, 0, 3));
        vbos.add(VertexBuffer.createVbo(textureArray, 1, 2));
        vbos.add(VertexBuffer.createVbo(normalsArray, 2, 3));
        vbos.add(VertexBuffer.createIndexVbo(indicesArray));

        // Unbind VAO
        glBindVertexArray(0);
    }

    @Override
    public void render()
    {
        if (ImGuiLayer.polygonMode.get() || gameObject.hasComponent(DirectionalLight.class))
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Bind the texture
        if (gameObject.material.hasTexture() == 1)
            gameObject.material.getTexture().bind();

        // Draw the mesh
        glBindVertexArray(vaoId);
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0);
        ImGuiLayer.drawCalls++;

        // Restore state
        if (gameObject.material.hasTexture() == 1)
            gameObject.material.getTexture().unbind();
        glBindVertexArray(0);

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
