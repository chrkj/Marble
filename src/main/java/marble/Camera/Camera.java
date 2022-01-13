package marble.camera;

import org.joml.Vector3f;

public class Camera {

    private Vector3f position;
    private Vector3f rotation;

    public final float Z_NEAR = 0.01f;
    public final float Z_FAR = 1000.f;
    public final float FOV = (float) Math.toRadians(60.0f);

    public Camera()
    {
        init(new Vector3f(), new Vector3f());
    }

    public Camera(Vector3f position)
    {
        init(position, new Vector3f());
    }

    public Camera(Vector3f position, Vector3f rotation)
    {
        init(position, rotation);
    }

    private void init(Vector3f position, Vector3f rotation)
    {
        this.position = position;
        this.rotation = rotation;
    }

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

    public Vector3f getPosition()
    {
        return position;
    }

    public Vector3f getRotation()
    {
        return rotation;
    }

}

