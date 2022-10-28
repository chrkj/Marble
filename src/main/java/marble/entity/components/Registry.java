package marble.entity.components;

import java.util.List;
import java.util.ArrayList;

import marble.entity.components.light.Light;
import marble.entity.components.light.SpotLight;
import marble.entity.components.light.PointLight;
import marble.entity.components.light.DirectionalLight;

public class Registry
{
    private final List<Mesh> meshes = new ArrayList<>();
    private final List<SpotLight> spotLights = new ArrayList<>();
    private final List<RigidBody> rigidBodies = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<DirectionalLight> directionalLights = new ArrayList<>();

    public Registry() { }

    public void register(Component component)
    {
        if (component instanceof Mesh mesh)
            meshes.add(mesh);
        else if (component instanceof SpotLight spotLight)
            spotLights.add(spotLight);
        else if (component instanceof PointLight pointLight)
            pointLights.add(pointLight);
        else if (component instanceof DirectionalLight directionalLight)
            directionalLights.add(directionalLight);
        else if (component instanceof RigidBody rigidBody)
            rigidBodies.add(rigidBody);
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

    public List<RigidBody> getRigidBodies()
    {
        return rigidBodies;
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
        if (component instanceof Mesh mesh)
            meshes.remove(mesh);
        else if (component instanceof SpotLight spotLight)
            spotLights.remove(spotLight);
        else if (component instanceof PointLight pointLight)
            pointLights.remove(pointLight);
        else if (component instanceof DirectionalLight directionalLight)
            directionalLights.remove(directionalLight);
        else if (component instanceof RigidBody rigidBody)
            rigidBodies.remove(rigidBody);
    }

}
