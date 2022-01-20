package marble.renderer;

import java.util.List;
import java.util.ArrayList;

import marble.Window;
import marble.util.Time;
import marble.util.Transformation;
import marble.camera.Camera;
import marble.gameobject.Material;
import marble.gameobject.GameObject;
import marble.gameobject.components.light.Light;
import marble.gameobject.components.light.SpotLight;
import marble.gameobject.components.light.PointLight;
import marble.gameobject.components.light.DirectionalLight;

import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    private final ArrayList<GameObject> gameObjects = new ArrayList<>();

    public Renderer()
    {
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
            // Get material and shader of current gameobject
            Material material = gameObject.material;
            Shader shader = material.getShader();
            shader.bind();
            shader.setUniformMaterial(material);

            // Set lighting uniforms
            for (int i = 0; i < lights.size(); i++) {
                if (lights.get(i).getClass().isAssignableFrom(DirectionalLight.class)) {
                    shader.setUniformDirLight(lights.get(i), Transformation.getViewMatrix(camera), i);
                } else if (lights.get(i).getClass().isAssignableFrom(SpotLight.class)) {
                    //shader.setUniformSpotLight(lights.get(i));
                } else if (lights.get(i).getClass().isAssignableFrom(PointLight.class)) {
                    //shader.setUniformPointLight(lights.get(i));
                }
            }

            // Set remaining uniforms
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));
            shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
            shader.setUniformMat4("uWorld", Transformation.getWorldMatrix(gameObject.transform.position, gameObject.transform.rotation, gameObject.transform.scale));
            shader.setUniform4f("ambientLight", Window.getCurrentScene().getAmbientLight());

            // Render
            gameObject.render();
            shader.unbind();
        }
    }

}
