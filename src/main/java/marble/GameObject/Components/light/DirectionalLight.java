package marble.gameobject.components.light;

import org.joml.Vector4f;

public class DirectionalLight extends Light {

    public DirectionalLight()
    {
        this.intensity = 1f;
        this.color = new Vector4f(1,1,1,1);
    }
}
