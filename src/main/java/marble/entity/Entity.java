package marble.entity;

import java.util.*;

import marble.editor.EditorLayer;
import marble.entity.components.Component;

public class Entity {

    public String name;
    public Transform transform;
    public final Map<Class<? extends Component>, Component> components = new HashMap<>();

    private int uuid;
    private transient Entity parent;
    private final List<Entity> children = new ArrayList<>();

    public Entity()
    {
        init("Empty Entity", new Transform(), Math.abs(UUID.randomUUID().hashCode()));
    }

    public Entity(int uuid)
    {
        init("Empty Entity", new Transform(), uuid);
    }

    public Entity(String name)
    {
        init(name, new Transform(), Math.abs(UUID.randomUUID().hashCode()));
    }

    private void init(String name, Transform transform, int uuid)
    {
        this.uuid = uuid;
        this.name = name;
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
        var comp = componentClass.cast(components.remove(componentClass));
        EditorLayer.currentScene.getRegistry().remove(comp);
        return comp;
    }

    public <T extends Component> void removeComponent(T component)
    {
        components.remove(component.getClass());
        EditorLayer.currentScene.getRegistry().remove(component);
    }

    public Entity addComponent(Component component)
    {
        components.put(component.getClass(), component);
        component.setEntity(this);

        return this;
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

    public Entity setScale(float x, float y, float z)
    {
        transform.setScale(x,y,z);
        return this;
    }

    public List<Entity> getChildren()
    {
        return children;
    }

    public Entity setParent(Entity entity)
    {
        this.parent = entity;
        entity.children.add(this);
        return this;
    }

    public Entity setChild(Entity entity)
    {
        this.children.add(entity);
        entity.parent = this;
        return this;
    }

    public int getUuid()
    {
        return uuid;
    }

}
