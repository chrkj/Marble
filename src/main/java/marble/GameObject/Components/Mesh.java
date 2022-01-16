package marble.gameobject.components;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import marble.renderer.VertexBuffer;
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
        ImGuiLayer.drawCalls++;

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
