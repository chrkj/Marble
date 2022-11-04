package marble.entity.components.camera;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.renderer.BatchRendering.Renderer2D;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import marble.gui.MarbleGui;
import marble.editor.EditorLayer;
import org.joml.Vector4f;

public class PerspectiveCamera extends Camera
{
    public float near = 0.3f;
    public float far = 1000.f;
    public float fov = 60.0f;

    @Override
    public Matrix4f getViewMatrix()
    {
        Vector3f pos = entity.transform.getPosition();
        Vector3f rot = entity.transform.getRotation();

        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(-rot.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(-rot.y), new Vector3f(0, 1, 0))
                .rotate((float)Math.toRadians(-rot.z), new Vector3f(0, 0, 1));
        viewMatrix.translate(-pos.x, -pos.y, -pos.z);
        return viewMatrix;
    }

    @Override
    public Matrix4f getProjectionMatrixGame()
    {
        float aspectRatio = EditorLayer.gameViewportSize.x / EditorLayer.gameViewportSize.y;
        return projectionMatrix.setPerspective((float) Math.toRadians(fov), aspectRatio, near, far);
    }

    @Override
    public Matrix4f getProjectionMatrixEditor()
    {
        float aspectRatio = EditorLayer.editorViewportSize.x / EditorLayer.editorViewportSize.y;
        return projectionMatrix.setPerspective((float) Math.toRadians(fov), aspectRatio, near, far);
    }

    @Override
    public void renderEntityInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Camera", nodeFlags);
        if (nodeOpen) {
            MarbleGui.text("Clipping planes");
            near = MarbleGui.dragFloat("Near", near);
            far = MarbleGui.dragFloat("Far", far);
            fov = MarbleGui.dragFloat("Fov", fov);
            ImGui.checkbox("Show frustum", shouldRenderFrustum);
            ImGui.treePop();
        }
    }

    @Override
    public void renderFrustum()
    {
        if (!shouldRenderFrustum.get()) return;

        var entPos = entity.transform.getPosition();
        var worldMatrix = entity.getWorldMatrix();

        var p0 = new Vector4f(+near/2, +near/2, -near, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);
        var p1 = new Vector4f(+near/2, -near/2, -near, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);
        var p2 = new Vector4f(-near/2, -near/2, -near, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);
        var p3 = new Vector4f(-near/2, +near/2, -near, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);

        var farFovOffset = (float)(far * Math.tan(Math.toRadians(fov/2)));
        var p4 = new Vector4f(+farFovOffset, +farFovOffset, -far, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);
        var p5 = new Vector4f(+farFovOffset, -farFovOffset, -far, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);
        var p6 = new Vector4f(-farFovOffset, -farFovOffset, -far, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);
        var p7 = new Vector4f(-farFovOffset, +farFovOffset, -far, 0).mul(worldMatrix).add(entPos.x, entPos.y, entPos.z, 0);

        Renderer2D.drawLine(p0, p1, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p1, p2, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p2, p3, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p3, p0, new Vector4f(1, 1, 1, 1));

        Renderer2D.drawLine(p4, p5, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p5, p6, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p6, p7, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p7, p4, new Vector4f(1, 1, 1, 1));

        Renderer2D.drawLine(p0, p4, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p1, p5, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p2, p6, new Vector4f(1, 1, 1, 1));
        Renderer2D.drawLine(p3, p7, new Vector4f(1, 1, 1, 1));
    }

    @Override
    public void cleanUp()
    {
    }

}
