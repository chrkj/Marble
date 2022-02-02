package marble.renderer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import marble.Window;
import marble.util.Time;
import marble.camera.Camera;
import marble.util.Transformation;
import marble.entity.Entity;
import marble.entity.Material;
import marble.entity.components.Component;
import marble.entity.components.Registry;
import marble.entity.components.light.DirectionalLight;
import marble.entity.components.light.Light;

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

    public void render(Camera camera, Registry registry)
    {
        clear();

        for (Entity entity : entities) {
            Material material = entity.material;
            Shader shader = material.getShader();
            shader.bind();

            int index = 0;
            for (Component light : registry.get(DirectionalLight.class)) {
                shader.setUniformDirLight((Light) light, Transformation.getViewMatrix(camera), index);
                index++;
            }

            shader.setUniformMaterial(material);
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniform3f("uAmbientLight", Window.getCurrentScene().getAmbientLight());
            shader.setUniform1f("uSpecularPower", Window.getCurrentScene().getSpecularPower());
            shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));
            shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
            shader.setUniformMat4("uWorld", Transformation.getWorldMatrix(entity.transform.position, entity.transform.rotation, entity.transform.scale));

            entity.render();
            shader.unbind();
        }
    }

}
