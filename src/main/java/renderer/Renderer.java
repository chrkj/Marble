package renderer;

import marble.Components.Mesh;
import marble.Components.SpriteRenderer;
import marble.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer()
    {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject gameObject)
    {
        SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
        if (spr != null)
            add(spr);
    }

    private void add(SpriteRenderer sprite)
    {
        boolean added = false;
        for (RenderBatch batch : batches ) {
            if (batch.hasRoom()) {
                batch.addSprite(sprite);
                added = true;
                break;
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
        }
    }

    public void render()
    {
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }

    public void renderMesh(Mesh mesh) {
        //clear();

        //if (window.isResized()) {
        //    glViewport(0, 0, window.getWidth(), window.getHeight());
        //    window.setResized(false);
        //}

        shaderProgram.bind();

        // Draw the mesh
        glBindVertexArray(mesh.getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, mesh.getVertexCount());

        // Restore state
        glBindVertexArray(0);

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }

}
