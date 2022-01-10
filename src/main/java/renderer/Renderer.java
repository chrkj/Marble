package renderer;

import marble.Camera.Camera;
import marble.Components.Mesh;
import marble.GameObject;
import marble.Transformation;
import marble.Window;
import org.joml.Matrix4f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Shader shader;
    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    public Renderer()
    {
        shader = new Shader("assets/shaders/default.shader");
        shader.compile();
    }

    public void add(GameObject gameObject)
    {
        gameObjects.add(gameObject);
    }

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Camera camera)
    {
        clear();

        if (Window.get().isResized()) {
            glViewport(0, 0, Window.get().getWidth(), Window.get().getHeight());
            Transformation.adjustProjectionMatrix(camera);
            Window.get().setResized(false);
        }

        shader.bind();

        // Update projection Matrix
        shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
        shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));

        // Render each gameItem
        for (GameObject gameObject : gameObjects) {
            // Set world matrix for this item
            Matrix4f worldMatrix = Transformation.getWorldMatrix(
                    gameObject.getTransform().position,
                    gameObject.getTransform().rotation,
                    gameObject.getTransform().scale);
            shader.setUniformMat4("uWorld", worldMatrix);
            // Render the mes for this game item
            gameObject.getComponent(Mesh.class).render();
        }

        shader.unbind();
    }

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
    }

}
