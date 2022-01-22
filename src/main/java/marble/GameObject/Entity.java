package marble.gameobject;

import java.util.*;

import marble.gameobject.components.Component;
import marble.gameobject.components.Texture;

import javax.swing.text.TabExpander;

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

    public Entity(String name, Transform transform)
    {
        init(name, transform, new Material());
    }

    public Entity(String name, Transform transform, Material material)
    {
        init(name, transform, material);
    }

    public Entity(String name, Material material)
    {
        init(name, new Transform(), material);
    }

    public void init(String name, Transform transform, Material material)
    {
        this.name = name;
        this.material = material;
        this.transform = transform;
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
        component.setGameObject(this);
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

}
