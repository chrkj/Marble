package marble.entity;

import java.util.*;

import marble.entity.components.Component;
import marble.entity.components.Texture;

public class Entity {

    public String name;
    public Material material;
    public Transform transform;

    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity()
    {
        init(null, new Transform(), new Material());
    }

    public Entity(String tag)
    {
        init(tag, new Transform(), new Material());
    }

    public void start()
    {
    }

    public void update(float dt)
    {
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        Component component = components.get(componentClass);
        if (component != null)
            return componentClass.cast(component);
        return null;
    }

    public <T extends Component> T removeComponent(Class<T> componentClass)
    {
        return componentClass.cast(components.remove(componentClass));
    }

    public Entity addComponent(Component component)
    {
        components.put(component.getClass(), component);
        component.setEntity(this);
        return this;
    }

    public void render()
    {
        for (Component component : getAllComponents())
            component.render();
    }

    public boolean hasComponent(Class<? extends Component> componentClass)
    {
        return components.containsKey(componentClass);
    }

    public Collection<Component> getAllComponents()
    {
        return components.values();
    }

    public void cleanUp()
    {
        for (Component component : getAllComponents())
            component.cleanUp();
        material.getShader().cleanUp();
    }

    public Entity setPosition(float x, float y, float z)
    {
        transform.setPosition(x,y,z);
        return this;
    }

    public Entity setRotation(float x, float y, float z)
    {
        transform.setRotation(x,y,z);
        return this;
    }

    public Entity addTexture(Texture texture)
    {
        material.setTexture(texture);
        return this;
    }

    private void init(String name, Transform transform, Material material)
    {
        this.name = name;
        this.material = material;
        this.transform = transform;
    }

}
