package marble.renderer;

import org.joml.Vector3f;
import org.joml.Vector4f;
import static org.lwjgl.opengl.GL11.*;

import marble.util.Time;
import marble.entity.Material;
import marble.entity.components.Mesh;
import marble.entity.components.Registry;
import marble.entity.components.camera.Camera;
import marble.renderer.BatchRendering.Renderer2D;

public class Renderer
{
    public enum ViewportId { EDITOR, GAME }

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
        Renderer2D.beginScene(camera);

        for (Mesh mesh : registry.getMeshes())
        {
            Material material = mesh.getMaterial();
            Shader shader = material.getShader();
            shader.bind();

            var dirLights = registry.getDirectionalLights();
            for (int i = 0; i < registry.getDirectionalLights().size(); i++)
                shader.setUniformDirLight(dirLights.get(i), camera.getViewMatrix(), i);

            var pointLights = registry.getPointLights();
            for (int i = 0; i < registry.getPointLights().size(); i++)
                shader.setUniformPointLight(pointLights.get(i), camera.getViewMatrix(), i);

            var spotLights = registry.getSpotLights();
            for (int i = 0; i < registry.getSpotLights().size(); i++)
                shader.setUniformSpotLight(spotLights.get(i), camera.getViewMatrix(), i);

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

            shader.setUniformMat4("uWorld", mesh.getEntity().getWorldMatrix());

            mesh.render();
            shader.unbind();
        }

        if (viewportId == ViewportId.EDITOR)
        {
            var rbs = registry.getRigidBodies();
            for (var rb : rbs)
                Renderer2D.drawRect(rb, new Vector4f(0,1,0,1));
        }

        Renderer2D.endScene();
        frameBuffer.unbind();
    }

}
