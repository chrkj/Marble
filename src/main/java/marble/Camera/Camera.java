package marble.Camera;

import marble.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    public Vector3f position;
    private final Matrix4f viewMatrix;
    private final Matrix4f worldMatrix;
    private static Matrix4f projectionMatrix;

    private static final float Z_NEAR = 0.01f;
    private static final float Z_FAR = 1000.f;
    private static final float FOV = (float) Math.toRadians(60.0f);

    public Camera(Vector3f position)
    {
        this.position = position;
        this.viewMatrix = new Matrix4f();
        this.worldMatrix = new Matrix4f();
        adjustProjectionMatrix();
    }

    public static void adjustProjectionMatrix()
    {
        float aspectRatio = (float) Window.get().getWidth() / Window.get().getHeight();
        projectionMatrix = new Matrix4f().setPerspective(FOV, aspectRatio, Z_NEAR, Z_FAR);
    }

    public Matrix4f getViewMatrix()
    {
        Vector3f cameraUp = new Vector3f(0.0f, 1.0f, 0.0f);
        Vector3f cameraFront = new Vector3f(0.0f, 0.0f, -1.0f);
        viewMatrix.identity();
        viewMatrix.lookAt(new Vector3f(position.x, position.y, position.z), cameraFront.add(position.x, position.y, position.z), cameraUp);
        return viewMatrix;
    }

    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, float scale) {
        return worldMatrix.translation(offset).
                rotateX((float)Math.toRadians(rotation.x)).
                rotateY((float)Math.toRadians(rotation.y)).
                rotateZ((float)Math.toRadians(rotation.z)).
                scale(scale);
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }
}

