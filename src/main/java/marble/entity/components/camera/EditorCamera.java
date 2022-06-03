package marble.entity.components.camera;

import marble.editor.EditorLayer;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class EditorCamera extends Camera {
    public final Vector3f position = new Vector3f();
    public final Vector3f rotation = new Vector3f();

    public float near = 0.3f;
    public float far = 1000.f;
    public float fov = (float) Math.toRadians(60.0f);

    public void move(float x, float y, float z)
    {
        if (z != 0) {
            position.x += Math.sin(Math.toRadians(rotation.y)) * -z * Math.cos(Math.toRadians(rotation.x));
            position.z += Math.cos(Math.toRadians(rotation.y)) *  z * Math.cos(Math.toRadians(rotation.x));
            position.y += Math.sin(Math.toRadians(rotation.x)) * z;
        }
        if (x != 0) {
            position.x += Math.sin(Math.toRadians(rotation.y - 90)) * -x;
            position.z += Math.cos(Math.toRadians(rotation.y - 90)) *  x;
        }
        position.y += y;
    }

    public void rotate(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;

        if (rotation.x > 90)
            rotation.x = 90;
        else if (rotation.x < -90)
            rotation.x = -90;
    }

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
        viewMatrix.identity();
        viewMatrix.rotate((float)Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(rotation.y), new Vector3f(0, 1, 0));
        viewMatrix.translate(-position.x, -position.y, -position.z);
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
    public void cleanUp()
    {
    }

    @Override
    public void setupInspector()
    {
    }
}
