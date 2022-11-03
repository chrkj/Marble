package marble.entity.components.light;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import marble.entity.components.Component;

public abstract class Light extends Component
{
    protected float intensity = 1;
    protected float gizmoLength = 2.5f;
    protected Vector4f color = new Vector4f(1,1,1,1);

    public Vector4f getColor()
    {
        return color;
    }

    public void setColor(float r, float g, float b, float a)
    {
        this.color.x = r;
        this.color.y = g;
        this.color.z = b;
        this.color.w = a;
    }

    public Vector3f getLightDir(Matrix4f viewMatrix)
    {
        var entRot = entity.transform.getRotation();
        var rotMat = new Matrix4f().identity()
                .rotate((float) Math.toRadians(entRot.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(entRot.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(entRot.z), new Vector3f(0, 0, 1));
        var lightDir = new Vector4f(0,-1,0,0).mul(rotMat).mul(viewMatrix);
        return new Vector3f(lightDir.x, lightDir.y, lightDir.z);
    }

    public void setColor(Vector4f color)
    {
        this.color = color;
    }

    public float getIntensity()
    {
        return intensity;
    }

    public void setIntensity(float intensity)
    {
        this.intensity = intensity;
    }

    public abstract void renderGizmo();

    @Override
    public abstract void renderEntityInspector();

    @Override
    public void cleanUp()
    {
    }

}
