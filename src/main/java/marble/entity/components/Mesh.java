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
import marble.gui.MarbleGui;
import marble.renderer.IndexBuffer;
import marble.renderer.VertexBuffer;

public class Mesh extends Component {

    public Material material;

    private String filePath = "";
    private transient final int vaoId;
    private transient final int vertexCount;
    private transient final int indicesCount;
    private transient IndexBuffer indexBuffer;
    private transient final List<VertexBuffer> vertexBufferObjectIds = new ArrayList<>();

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
        vertexBufferObjectIds.add(VertexBuffer.create(verticesArray, 0, 3));
        vertexBufferObjectIds.add(VertexBuffer.create(textureArray, 1, 2));
        vertexBufferObjectIds.add(VertexBuffer.create(normalsArray, 2, 3));
        indexBuffer = IndexBuffer.create(indicesArray);

        // Unbind VAO
        glBindVertexArray(0);
    }

    public void render()
    {
        if (MarbleGui.polygonMode.get())
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

        // Bind the texture
        if (material.hasTexture() == 1)
            material.getTexture().bind();

        // Draw mesh if vao is valid
        if (vaoId != -1)
        {
            glBindVertexArray(vaoId);
            glDrawElements(GL_TRIANGLES, indicesCount, GL_UNSIGNED_INT, 0);
            MarbleGui.drawCalls++;
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
        for (VertexBuffer vbo : vertexBufferObjectIds)
            vbo.delete();
        indexBuffer.delete();

        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);

        material.getShader().cleanUp();
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Mesh", nodeFlags);
        if (nodeOpen)
        {
            MarbleGui.text("File: " + filePath);
            MarbleGui.text("VaoID: " + vaoId);
            MarbleGui.text("Vertex count: " + vertexCount);
            MarbleGui.text("Indices count: " + indicesCount);
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
