package marble.renderer;

import marble.Window;
import marble.camera.Camera;
import marble.util.Transformation;
import marble.gameobject.GameObject;
import marble.gameobject.components.Mesh;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final Shader shader;
    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    float scale;
    Vector3f rot = new Vector3f();
    Vector3f pos = new Vector3f();

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

        // Update projection / view Matrix
        shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
        shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));

        // Upload texture sampler
        shader.setUniform1i("texture_sampler", 0);

        // Render game objects
        for (GameObject gameObject : gameObjects) {
            gameObject.transform.position.add(gameObject.imGuiOffsetPos, pos);
            gameObject.transform.rotation.add(gameObject.imGuiOffsetRot, rot);
            scale = gameObject.transform.scale + gameObject.imGuiOffsetScale;

            Matrix4f worldMatrix = Transformation.getWorldMatrix(pos, rot, scale);
            shader.setUniformMat4("uWorld", worldMatrix);

            gameObject.render();
        }
        shader.unbind();
    }

    public void cleanUp()
    {
        shader.cleanUp();
    }

}
