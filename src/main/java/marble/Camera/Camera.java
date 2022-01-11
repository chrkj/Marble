package Marble.Camera;

import org.joml.Vector3f;

public class Camera {

    public Vector3f position;
    public Vector3f rotation;

    public final float Z_NEAR = 0.01f;
    public final float Z_FAR = 1000.f;
    public final float FOV = (float) Math.toRadians(60.0f);

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

    public void move(float x, float y, float z, float dt)
    {
        if (z != 0) {
            position.x += ((float) Math.sin(Math.toRadians(rotation.y)) * -1.0f * z) * dt;
            position.z += ((float) Math.cos(Math.toRadians(rotation.y)) * z) * dt;
        }
        if (x != 0) {
            position.x += ((float) Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * x) * dt;
            position.z += ((float) Math.cos(Math.toRadians(rotation.y - 90)) * x) * dt;
        }
        position.y += y * dt;
    }

    public void rotate(float x, float y, float z, float dt)
    {
        rotation.x += x * dt;
        rotation.y += y * dt;
        rotation.z += z * dt;
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

