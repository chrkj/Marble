package marble.gameobject.components.light;

public class LightFactory {

    public static Light getLight(LightType type)
    {
        return type.getConstructor().get();
    }

}
