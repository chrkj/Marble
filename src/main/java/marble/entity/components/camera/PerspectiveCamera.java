package marble.entity.components.camera;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import marble.EditorLayer;
import marble.imgui.ImGuiLayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class PerspectiveCamera extends Camera {

    public float near = 0.3f;
    public float far = 1000.f;
    public float fov = (float) Math.toRadians(60.0f);

    @Override
    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, Vector3f scale)
    {
        return worldMatrix.translation(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
    }

    @Override
    public Matrix4f getViewMatrix()
    {
        Vector3f pos = entity.transform.position;
        Vector3f rot = entity.transform.rotation;

        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(rot.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rot.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-pos.x, -pos.y, -pos.z);
        return viewMatrix;
    }

    @Override
    public Matrix4f getProjectionMatrixGame()
    {
        float aspectRatio = EditorLayer.gameViewportSize.x / EditorLayer.gameViewportSize.y;
        return projectionMatrix.setPerspective(fov, aspectRatio, near, far);
    }

    @Override
    public Matrix4f getProjectionMatrixScene()
    {
        float aspectRatio = EditorLayer.sceneViewportSize.x / EditorLayer.sceneViewportSize.y;
        return projectionMatrix.setPerspective(fov, aspectRatio, near, far);
    }

    @Override
    public void setupInspector()
    {
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Camera", nodeFlags);
        if (nodeOpen) {
            ImGuiLayer.text("Clipping planes");
            near = ImGuiLayer.dragFloat("Near", near);
            far = ImGuiLayer.dragFloat("Far", far);
            fov = ImGuiLayer.dragFloat("Fov", fov);
            ImGui.treePop();
        }
    }

    @Override
    public void cleanUp()
    {
    }

}
