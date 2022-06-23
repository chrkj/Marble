package marble.renderer;

import java.util.List;

import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL44.glClearTexImage;

import marble.util.Time;
import marble.editor.EditorLayer;
import marble.entity.Material;
import marble.entity.components.Mesh;
import marble.entity.components.Registry;
import marble.entity.components.light.Light;
import marble.entity.components.camera.Camera;

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

            List<Light> lights = registry.getLights();
            for (int i = 0; i < registry.getLights().size(); i++)
                shader.setUniformDirLight(lights.get(i), camera.getViewMatrix(), i);

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

}
