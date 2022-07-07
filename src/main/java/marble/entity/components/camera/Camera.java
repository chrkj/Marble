package marble.entity.components.camera;

import org.joml.Matrix4f;

import marble.entity.components.Component;

public abstract class Camera extends Component {

    protected final transient Matrix4f viewMatrix = new Matrix4f();
    protected final transient Matrix4f worldMatrix = new Matrix4f();
    protected final transient Matrix4f projectionMatrix = new Matrix4f();

    public Camera() { }

    public abstract Matrix4f getViewMatrix();
    public abstract Matrix4f getProjectionMatrixGame();
    public abstract Matrix4f getProjectionMatrixEditor();

}

