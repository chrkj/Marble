package marble.renderer;

import java.util.List;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import marble.Window;
import marble.util.Time;
import marble.util.Transformation;
import marble.camera.Camera;
import marble.entity.Material;
import marble.entity.Entity;
import marble.entity.components.light.Light;
import marble.entity.components.light.SpotLight;
import marble.entity.components.light.PointLight;
import marble.entity.components.light.DirectionalLight;

public class Renderer {

    private final ArrayList<Entity> entities = new ArrayList<>();

    public void add(Entity entity)
    {
        entities.add(entity);
    }

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Camera camera, List<Light> lights)
    {
        clear();

        for (Entity entity : entities) {
            Material material = entity.material;
            Shader shader = material.getShader();
            shader.bind();

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

            shader.setUniformMaterial(material);
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniform4f("uAmbientLight", Window.getCurrentScene().getAmbientLight());
            shader.setUniform1f("uSpecularPower", Window.getCurrentScene().getSpecularPower());
            shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));
            shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
            shader.setUniformMat4("uWorld", Transformation.getWorldMatrix(entity.transform.position, entity.transform.rotation, entity.transform.scale));

            entity.render();
            shader.unbind();
        }
    }

}
