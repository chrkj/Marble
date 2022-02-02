package marble.util;

import org.joml.Vector3f;
import org.joml.Matrix4f;

import marble.camera.Camera;
import marble.imgui.ImGuiLayer;

public class Transformation {

    private static final Matrix4f viewMatrix = new Matrix4f();
    private static final Matrix4f worldMatrix = new Matrix4f();
    private static final Matrix4f projectionMatrix = new Matrix4f();

    public static Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, Vector3f scale)
    {
        return worldMatrix.translation(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale.x, scale.y, scale.z);
    }

    public static Matrix4f getViewMatrix(Camera camera)
    {
        Vector3f cameraPos = camera.getPosition();
        Vector3f rotation = camera.getRotation();

        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        return viewMatrix;
    }

    public static Matrix4f getProjectionMatrixScene(Camera camera)
    {
        float aspectRatio = ImGuiLayer.sceneViewportSize.x / ImGuiLayer.sceneViewportSize.y;
        return projectionMatrix.setPerspective(camera.FOV, aspectRatio, camera.Z_NEAR, camera.Z_FAR);
    }

    public static Matrix4f getProjectionMatrixGame(Camera camera)
    {
        float aspectRatio = ImGuiLayer.gameViewportSize.x / ImGuiLayer.gameViewportSize.y;
        return projectionMatrix.setPerspective(camera.FOV, aspectRatio, camera.Z_NEAR, camera.Z_FAR);
    }
}
