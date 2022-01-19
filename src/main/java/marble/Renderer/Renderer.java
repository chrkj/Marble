package marble.renderer;

import java.util.List;
import java.util.ArrayList;

import marble.gameobject.components.Material;
import marble.gameobject.components.light.DirectionalLight;
import marble.gameobject.components.light.PointLight;
import marble.gameobject.components.light.SpotLight;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import marble.Window;
import marble.util.Time;
import marble.util.Transformation;
import marble.camera.Camera;
import marble.gameobject.GameObject;
import marble.gameobject.components.Texture;
import marble.gameobject.components.light.Light;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    // TODO: Add cleanUp for shader!
    private final Shader defaultShader = new Shader("assets/shaders/default.glsl");
    private final ArrayList<GameObject> gameObjects = new ArrayList<>();
    private final Vector3f defaultColor = new Vector3f(0.85f, 0.1f, 0.74f);

    public Renderer()
    {
        defaultShader.compile();
    }

    public void add(GameObject gameObject)
    {
        gameObjects.add(gameObject);
    }

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Camera camera, List<Light> lights)
    {
        clear();
        if (Window.isResized()) {
            glViewport(0, 0, Window.getWidth(), Window.getHeight());
            Transformation.adjustProjectionMatrix(camera);
            Window.setResized(false);
        }

        // Render game objects
        for (GameObject gameObject : gameObjects) {
            Shader shader = defaultShader;

            if (gameObject.hasComponent(Material.class)) {
                Material material = gameObject.getComponent(Material.class);
                shader = material.getShader();
                shader.bind();
                shader.setUniformMaterial(material);
            }


            for (int i = 0; i < lights.size(); i++) {
                if (lights.get(i).getClass().isAssignableFrom(DirectionalLight.class)) {
                    shader.setUniformDirLight(lights.get(i), i);
                } else if (lights.get(i).getClass().isAssignableFrom(SpotLight.class)) {
                    //shader.setUniformSpotLight(lights.get(i));
                } else if (lights.get(i).getClass().isAssignableFrom(PointLight.class)) {
                    //shader.setUniformPointLight(lights.get(i));
                }
            }

            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));
            shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
            Matrix4f worldMatrix = Transformation.getWorldMatrix(gameObject.transform.position, gameObject.transform.rotation, gameObject.transform.scale);
            shader.setUniformMat4("uWorld", worldMatrix);


            gameObject.render();
            shader.unbind();
        }
    }

}
