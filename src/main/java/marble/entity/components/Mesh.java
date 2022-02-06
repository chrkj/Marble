package marble.entity.components;

import java.util.List;
import java.util.ArrayList;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL30.*;

import marble.entity.Material;
import marble.renderer.Buffer;
import marble.imgui.ImGuiLayer;

public class Mesh extends Component {

    public Material material;

    private String filePath = "";
    private final int vaoId;
    private final int vertexCount;
    private final int indicesCount;
    private final List<Integer> vertexBufferObjectIds = new ArrayList<>();

    public Mesh()
    {
        this.vaoId = -1;
        this.indicesCount = 0;
        this.vertexCount = 0;
        this.material = new Material();
    }

    public Mesh(float[] verticesArray, float[] textureArray, int[] indicesArray, float[] normalsArray, String filePath)
    {
        this.filePath = filePath;
        this.material = new Material();
        this.vertexCount = verticesArray.length / 3;
        this.indicesCount = indicesArray.length;

        // Create and bind VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Create VBO's
        vertexBufferObjectIds.add(Buffer.createVertexBuffer(verticesArray, 0, 3));
        vertexBufferObjectIds.add(Buffer.createVertexBuffer(textureArray, 1, 2));
        vertexBufferObjectIds.add(Buffer.createVertexBuffer(normalsArray, 2, 3));
        vertexBufferObjectIds.add(Buffer.createIndexBuffer(indicesArray));

        // Unbind VAO
        glBindVertexArray(0);
    }

    public void render()
    {
        if (ImGuiLayer.polygonMode.get())
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Bind the texture
        if (material.hasTexture() == 1)
            material.getTexture().bind();

        // Draw mesh if vao is valid
        if (vaoId != -1) {
            glBindVertexArray(vaoId);
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
            ImGuiLayer.drawCalls++;
        }

        // Restore state
        if (material.hasTexture() == 1)
            material.getTexture().unbind();

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

        material.getShader().cleanUp();
    }

    @Override
    public void setupInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Mesh", nodeFlags);
        if (nodeOpen) {
            ImGuiLayer.text("File: " + filePath);
            ImGuiLayer.text("VaoID: " + vaoId);
            ImGuiLayer.text("Vertex count: " + vertexCount);
            ImGuiLayer.text("Indices count: " + indicesCount);
            ImGui.treePop();
        }
        material.setupInspector();
    }

    public int getVertexCount()
    {
        return vertexCount;
    }

    public void setAmbient(float r, float g, float b, float a)
    {
        material.setAmbient(r,g,b,a);
    }

    public void setDiffuse(float r, float g, float b, float a)
    {
        material.setDiffuse(r,g,b,a);
    }

    public void setReflectance(float reflectance)
    {
        material.setReflectance(reflectance);
    }

    public void addTexture(Texture texture)
    {
        material.setTexture(texture);
    }

}
