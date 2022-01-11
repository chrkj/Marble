package Marble.Renderer;

import Marble.Window;
import Marble.Camera.Camera;
import Marble.util.Transformation;
import Marble.GameObject.GameObject;
import Marble.GameObject.Components.Mesh;

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
        if (Window.isResized()) {
            glViewport(0, 0, Window.getWidth(), Window.getHeight());
            Transformation.adjustProjectionMatrix(camera);
            Window.setResized(false);
        }

        shader.bind();

        // Update projection Matrix
        shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
        shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));

        // Render game objects
        for (GameObject gameObject : gameObjects) {
            Matrix4f worldMatrix = Transformation.getWorldMatrix(
                    gameObject.transform.position,
                    gameObject.transform.rotation,
                    gameObject.transform.scale);
            shader.setUniformMat4("uWorld", worldMatrix);
            if (gameObject.getComponent(Mesh.class) != null)
                gameObject.getComponent(Mesh.class).render();
        }
        shader.unbind();
    }

    public void cleanUp()
    {
        shader.cleanUp();
    }

}
