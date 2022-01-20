package marble.gameobject.components.light;

import org.joml.Vector4f;

import marble.gameobject.components.Component;

public abstract class Light extends Component {

    protected Vector4f color;
    protected float intensity;

    public Light()
    {
    }

    @Override
    public void render()
    {
    }

    @Override
    public void cleanUp()
    {
    }

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

}
