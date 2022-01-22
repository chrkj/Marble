package marble.entity;

import org.joml.Vector3f;

public class Transform {

    public Vector3f scale;
    public Vector3f position;
    public Vector3f rotation;

    public Transform()
    {
        init(new Vector3f(), new Vector3f(), new Vector3f(1,1,1));
    }

    public Transform(Vector3f position, Vector3f rotation)
    {
        init(position, rotation, new Vector3f(1,1,1));
    }

    public Transform(Vector3f position)
    {
        init(position, new Vector3f(), new Vector3f(1,1,1));
    }

    public Transform(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        init(position, rotation, scale);
    }

    private void init(Vector3f position, Vector3f rotation, Vector3f scale)
    {
        this.scale = scale;
        this.position = position;
        this.rotation = rotation;
    }

    public void setRotation(float x, float y, float z)
    {
        rotation.x = x;
        rotation.y = y;
        rotation.z = z;
    }

    public void setPosition(float x, float y, float z)
    {
        position.x = x;
        position.y = y;
        position.z = z;
    }

    public void setScale(float x, float y, float z)
    {
        this.scale.x = x;
        this.scale.y = y;
        this.scale.z = z;
    }

}
