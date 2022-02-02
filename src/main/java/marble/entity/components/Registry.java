package marble.entity.components;

import java.util.List;
import java.util.ArrayList;

import marble.entity.components.light.Light;
import marble.entity.components.light.SpotLight;
import marble.entity.components.light.PointLight;
import marble.entity.components.light.DirectionalLight;

public class Registry {

    private final List<Mesh> meshes = new ArrayList<>();
    private final List<SpotLight> spotLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<DirectionalLight> directionalLights = new ArrayList<>();

    public Registry()
    {
    }

    public void register(Component component)
    {
        if (component instanceof Mesh)
            meshes.add((Mesh) component);
        if (component instanceof SpotLight)
            spotLights.add((SpotLight) component);
        if (component instanceof PointLight)
            pointLights.add((PointLight) component);
        if (component instanceof DirectionalLight)
            directionalLights.add((DirectionalLight) component);
    }

    public List<? extends Component> get(Class<? extends Component> componentClass)
    {
        if (componentClass == Mesh.class)
            return meshes;
        if (componentClass == SpotLight.class)
            return spotLights;
        if (componentClass == PointLight.class)
            return pointLights;
        if (componentClass == DirectionalLight.class)
            return directionalLights;
        if (componentClass == Light.class) {
            List<Light> lights = new ArrayList<>();
            lights.addAll(spotLights);
            lights.addAll(pointLights);
            lights.addAll(directionalLights);
            return lights;
        }
        return null;
    }

    public void remove(Component component)
    {
        if (component instanceof Mesh)
            meshes.remove((Mesh) component);
        else if (component instanceof SpotLight)
            spotLights.remove((SpotLight) component);
        else if (component instanceof PointLight)
            pointLights.remove((PointLight) component);
        else if (component instanceof DirectionalLight)
            directionalLights.remove((DirectionalLight) component);
    }

}
