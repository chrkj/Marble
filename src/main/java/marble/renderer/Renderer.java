package marble.renderer;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;

import marble.util.Time;
import marble.entity.Material;
import marble.entity.components.Mesh;
import marble.entity.components.Registry;
import marble.entity.components.light.Light;
import marble.entity.components.camera.Camera;
import marble.entity.components.light.DirectionalLight;
import marble.entity.components.light.PointLight;
import marble.entity.components.light.SpotLight;

public class Renderer {

    public enum ViewportId { EDITOR, GAME };

    public void clear()
    {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        //glClearTexImage(EditorLayer.editorViewportFb.getColorAttachmentRendererID(1), 0, GL_RED_INTEGER, GL_INT, new int[]{ -1 }); // TODO: FIX clear tex image not working properly
    }

    public void render(Camera camera, Registry registry, Framebuffer frameBuffer, Vector3f ambientLight, float specularPower, ViewportId viewportId)
    {
        frameBuffer.bind();
        clear();

        for (Mesh mesh : registry.getMeshes())
        {
            Material material = mesh.getMaterial();
            Shader shader = material.getShader();
            shader.bind();

            var lights = registry.getLights();
            for (int i = 0; i < registry.getLights().size(); i++)
                addLightUniforms(shader, lights.get(i), camera, i);

            shader.setUniformMaterial(material);
            shader.setUniform1f("uTime", Time.getTime());
            shader.setUniform1i("uTextureSampler", 0);
            shader.setUniform1i("uEntityID", mesh.getEntity().getUuid());
            shader.setUniform3f("uAmbientLight", ambientLight);
            shader.setUniform1f("uSpecularPower", specularPower);
            shader.setUniformMat4("uView", camera.getViewMatrix());

            if (viewportId == ViewportId.GAME)
                shader.setUniformMat4("uProjection", camera.getProjectionMatrixGame());
            else
                shader.setUniformMat4("uProjection", camera.getProjectionMatrixEditor());

            shader.setUniformMat4("uWorld", camera.getWorldMatrix(mesh.getEntity().transform.position, mesh.getEntity().transform.rotation, mesh.getEntity().transform.scale));

            mesh.render();
            shader.unbind();
        }
        frameBuffer.unbind();
    }

    private void addLightUniforms(Shader shader, Light light, Camera camera, int i)
    {
        if (light.getClass() == DirectionalLight.class)
            shader.setUniformDirLight(light, camera.getViewMatrix(), i);
        else if (light.getClass() == PointLight.class)
            shader.setUniformPointLight((PointLight) light, camera.getViewMatrix(), i);
        else if (light.getClass() == SpotLight.class)
            shader.SetUniformSpotLight((SpotLight) light, camera.getViewMatrix(), i);
    }

}
