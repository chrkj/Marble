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

    public List<Mesh> getMeshes()
    {
        return meshes;
    }

    public List<SpotLight> getSpotLights()
    {
        return spotLights;
    }

    public List<PointLight> getPointLights()
    {
        return pointLights;
    }

    public List<DirectionalLight> getDirectionalLights()
    {
        return directionalLights;
    }

    public List<Light> getLights()
    {
        List<Light> lights = new ArrayList<>();
        lights.addAll(spotLights);
        lights.addAll(pointLights);
        lights.addAll(directionalLights);
        return lights;
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
