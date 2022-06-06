package marble.entity.components.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
    @Override
    public void cleanUp()
    {

    }

    @Override
    public void setupInspector()
    {

    }

    @Override
    public Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, Vector3f scale)
    {
        return null;
    }

    @Override
    public Matrix4f getViewMatrix()
    {
        return null;
    }

    @Override
    public Matrix4f getProjectionMatrixGame()
    {
        return null;
    }

    @Override
    public Matrix4f getProjectionMatrixEditor()
    {
        return null;
    }
}
