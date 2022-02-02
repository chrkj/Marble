package marble.renderer;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

import marble.Window;
import marble.entity.components.Mesh;
import marble.entity.components.light.Light;
import marble.util.Time;
import marble.camera.Camera;
import marble.util.Transformation;
import marble.entity.Material;
import marble.entity.components.Registry;

public class Renderer {

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Camera camera, Registry registry)
    {
        clear();

        for (Mesh mesh : registry.getMeshes()) {
            Material material = mesh.material;
            Shader shader = material.getShader();
            shader.bind();

            int index = 0;
            for (Light light : registry.getLights()) {
                shader.setUniformDirLight(light, Transformation.getViewMatrix(camera), index);
                index++;
            }

            shader.setUniformMaterial(material);
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniform3f("uAmbientLight", Window.getCurrentScene().getAmbientLight());
            shader.setUniform1f("uSpecularPower", Window.getCurrentScene().getSpecularPower());
            shader.setUniformMat4("uView", Transformation.getViewMatrix(camera));
            shader.setUniformMat4("uProjection", Transformation.getProjectionMatrix(camera));
            shader.setUniformMat4("uWorld", Transformation.getWorldMatrix(mesh.getEntity().transform.position, mesh.getEntity().transform.rotation, mesh.getEntity().transform.scale));

            mesh.getEntity().render();
            shader.unbind();
        }
    }

}
