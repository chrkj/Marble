package marble.gameobject;

import java.util.*;

import marble.gameobject.components.Component;

public class GameObject {

    public String name;
    public Material material;
    public Transform transform;

    private final Map<Class<?>, Component> components = new HashMap<>();

    public GameObject(String name)
    {
        init(name, new Transform(), new Material());
    }

    public GameObject(String name, Transform transform)
    {
        init(name, transform, new Material());
    }

    public GameObject(String name, Transform transform, Material material)
    {
        init(name, transform, material);
    }

    public GameObject(String name, Material material)
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

    public void addComponent(Component component)
    {
        components.put(component.getClass(), component);
        component.setGameObject(this);
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
}
