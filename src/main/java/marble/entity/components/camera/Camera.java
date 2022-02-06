package marble.entity.components.camera;

import org.joml.Matrix4f;

import marble.entity.components.Component;
import org.joml.Vector3f;

public abstract class Camera extends Component {

    protected final Matrix4f viewMatrix = new Matrix4f();
    protected final Matrix4f worldMatrix = new Matrix4f();
    protected final Matrix4f projectionMatrix = new Matrix4f();

    public Camera()
    {
    }

    public abstract Matrix4f getViewMatrix();
    public abstract Matrix4f getProjectionMatrixGame();
    public abstract Matrix4f getProjectionMatrixScene();
    public abstract Matrix4f getWorldMatrix(Vector3f offset, Vector3f rotation, Vector3f scale);

}

