package marble;

import org.joml.Vector3f;

public class Transform {

    public float scale;
    public Vector3f position;
    public Vector3f rotation;

    public Transform()
    {
        init(new Vector3f(), new Vector3f(), 1);
    }

    public Transform(Vector3f position)
    {
        init(position, new Vector3f(), 1);
    }

    public Transform(Vector3f position, float scale)
    {
        init(position, new Vector3f(), scale);
    }

    public Transform(Vector3f position, Vector3f rotation, float scale)
    {
        init(position, rotation, scale);
    }

    private void init(Vector3f position, Vector3f rotation, float scale)
    {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public void setRotation(Vector3f rotation)
    {
        this.rotation = rotation;
    }
}
