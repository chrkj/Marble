package marble.entity.components.camera;

import org.joml.Matrix4f;

public class OrthographicCamera extends Camera {
    @Override
    public void cleanUp()
    {

    }

    @Override
    public void renderEntityInspector()
    {

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
