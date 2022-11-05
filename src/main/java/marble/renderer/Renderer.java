package marble.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE1;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL30C.GL_RED_INTEGER;
import static org.lwjgl.opengl.GL44.glClearTexImage;

import marble.util.Time;
import marble.entity.Material;
import marble.editor.EditorLayer;
import marble.entity.components.Mesh;
import marble.entity.components.Registry;
import marble.entity.components.light.Light;
import marble.entity.components.camera.Camera;
import marble.renderer.BatchRendering.Renderer2D;
import static marble.editor.EditorLayer.depthMapFb;

public class Renderer
{
    public enum ViewportId { EDITOR, GAME }

    private Matrix4f lightSpaceMatrix;
    private final Shader depthMapShader = new Shader("assets/shaders/depthMapShader.glsl");

    public void clear(ViewportId viewportId)
    {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        if (viewportId == ViewportId.EDITOR)
            glClearTexImage(EditorLayer.editorViewportFb.getColorAttachmentRendererID(1), 0, GL_RED_INTEGER, GL_INT, new int[]{ -1 });

    }

    public void render(Camera camera, Registry registry, Framebuffer frameBuffer, Vector3f ambientLight, float specularPower, ViewportId viewportId)
    {
        // Render depth map
        if (ViewportId.EDITOR == viewportId) {
            depthMapFb.bind();
            depthMapShader.bind();
            glClear(GL_DEPTH_BUFFER_BIT);

            float near_plane = 0.3f, far_plane = 1000f;
            Matrix4f lightProjection = new Matrix4f().setOrtho(-20f, 20f, -20f, 20f, near_plane, far_plane);

            var dirLight = registry.getDirectionalLights().get(0);
            var entRot = dirLight.getEntity().transform.getRotation();
            var rotMat = new Matrix4f().identity()
                    .rotate((float) Math.toRadians(entRot.x), new Vector3f(1, 0, 0))
                    .rotate((float) Math.toRadians(entRot.y), new Vector3f(0, 1, 0))
                    .rotate((float) Math.toRadians(entRot.z), new Vector3f(0, 0, 1));
            var lightDir = new Vector4f(0,-1,0,0).mul(rotMat);

            Matrix4f lightView = new Matrix4f().lookAt(
                    new Vector3f(dirLight.getEntity().transform.getPosition()),
                    new Vector3f(lightDir.x, lightDir.y, lightDir.z),
                    new Vector3f(0.0f, 1.0f, 0.0f));

            lightSpaceMatrix = lightProjection.mul(lightView);

            depthMapShader.setUniformMat4("lightSpaceMatrix", lightSpaceMatrix);


            for (Mesh mesh : registry.getMeshes()) {
                depthMapShader.setUniformMat4("model", mesh.getEntity().getWorldMatrix());
                mesh.render();
            }

            depthMapShader.unbind();
            depthMapFb.unbind();
        }

        // Render scene
        frameBuffer.bind();
        clear(viewportId);
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
            shader.setUniform1i("shadowMap", 1);
            glActiveTexture(GL_TEXTURE1);
            glBindTexture(GL_TEXTURE_2D, depthMapFb.getDepthAttachmentID());
            shader.setUniform1i("uEntityID", mesh.getEntity().getUuid());
            shader.setUniform3f("uAmbientLight", ambientLight);
            shader.setUniform1f("uSpecularPower", specularPower);
            shader.setUniformMat4("uView", camera.getViewMatrix());
            shader.setUniformMat4("uLightSpaceMatrix", lightSpaceMatrix);



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
                rb.renderCollider();
            for (Light light : registry.getLights())
                light.renderGizmo();
            for (Camera cam : registry.getCameras())
                cam.renderFrustum();
        }


        Renderer2D.endScene();
        frameBuffer.unbind();
    }

}
