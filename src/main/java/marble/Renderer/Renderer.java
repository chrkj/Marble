package marble.renderer;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import marble.Window;
import marble.camera.Camera;
import marble.gameobject.components.Texture;
import marble.util.Transformation;
import marble.gameobject.GameObject;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Shader shader;
    private final Vector3f defaultColor = new Vector3f(0.85f, 0.1f, 0.74f);
    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    Vector3f rot = new Vector3f();

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

        // Upload uniforms
        shader.setUniform1i("uTextureSampler", 0);
        shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));
        shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));

        // Render game objects
        for (GameObject gameObject : gameObjects) {
            Matrix4f worldMatrix = Transformation.getWorldMatrix(gameObject.transform.position, gameObject.transform.rotation, gameObject.transform.scale);
            shader.setUniformMat4("uWorld", worldMatrix);

            // TODO: Fix (slow)
            if (gameObject.hasComponent(Texture.class)) {
                shader.setUniform1i("useColor", 0);
            } else {
                shader.setUniform1i("useColor", 1);
                shader.setUniform3f("uColor", defaultColor);
            }

            gameObject.render();
        }
        shader.unbind();
    }

    public void cleanUp()
    {
        shader.cleanUp();
    }

}
