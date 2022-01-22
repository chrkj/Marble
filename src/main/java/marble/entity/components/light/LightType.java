package marble.entity.components.light;

import java.util.function.Supplier;

public enum LightType {

    SPOT(SpotLight::new),
    POINT(PointLight::new),
    DIRECTIONAL(DirectionalLight::new);

    private final Supplier<Light> constructor;

    LightType(Supplier<Light> constructor)
    {
        this.constructor = constructor;
    }

    public Supplier<Light> getConstructor()
    {
        return this.constructor;
    }

}