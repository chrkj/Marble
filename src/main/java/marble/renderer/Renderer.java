package marble.renderer;

import static org.lwjgl.opengl.GL11.*;

import marble.EditorLayer;
import marble.util.Time;
import marble.entity.Material;
import marble.entity.components.Mesh;
import marble.entity.components.Registry;
import marble.entity.components.light.Light;
import marble.entity.components.camera.Camera;

public class Renderer {

    public void clear()
    {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Camera camera, Registry registry, FrameBuffer frameBuffer, int bufferId)
    {
        frameBuffer.bind();
        clear();

        for (Mesh mesh : registry.getMeshes()) {
            Material material = mesh.material;
            Shader shader = material.getShader();
            shader.bind();

            int index = 0;
            for (Light light : registry.getLights()) {
                shader.setUniformDirLight(light, camera.getViewMatrix(), index);
                index++;
            }

            shader.setUniformMaterial(material);
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniform3f("uAmbientLight", EditorLayer.currentScene.getAmbientLight());
            shader.setUniform1f("uSpecularPower", EditorLayer.currentScene.getSpecularPower());
            shader.setUniformMat4("uView", camera.getViewMatrix());
            if (bufferId == 0)
                shader.setUniformMat4("uProjection", camera.getProjectionMatrixGame());
            else
                shader.setUniformMat4("uProjection", camera.getProjectionMatrixScene());

            shader.setUniformMat4("uWorld", camera.getWorldMatrix(mesh.getEntity().transform.position, mesh.getEntity().transform.rotation, mesh.getEntity().transform.scale));

            mesh.render();
            shader.unbind();
        }
        frameBuffer.unbind();
    }

}
