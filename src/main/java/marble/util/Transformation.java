package Marble.util;

import Marble.Camera.Camera;

import Marble.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Transformation {

    private static final Matrix4f viewMatrix = new Matrix4f();
    private static final Matrix4f worldMatrix = new Matrix4f();
    private static final Matrix4f projectionMatrix = new Matrix4f();

    public static Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale)
    {
        return worldMatrix.translation(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
    }

    public static void adjustProjectionMatrix(Camera camera)
    {
        float aspectRatio = (float) Window.getWidth() / Window.getHeight();
        projectionMatrix.setPerspective(camera.FOV, aspectRatio, camera.Z_NEAR, camera.Z_FAR);
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

    public static Matrix4f getProjectionMatrix(Camera camera)
    {
        return projectionMatrix.setPerspective(camera.FOV, (float) Window.getWidth() / Window.getHeight(), camera.Z_NEAR, camera.Z_FAR);
    }
}
