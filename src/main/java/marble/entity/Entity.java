package marble.entity;

import java.util.*;

import marble.imgui.ImGuiLayer;
import marble.entity.components.Texture;
import marble.entity.components.Component;

public class Entity {

    public String name;
    public Material material;
    public Transform transform;

    private Entity parent = null; // TODO: Implement this
    private final List<Entity> children = new ArrayList<>(); // TODO: Implement this
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity()
    {
        init(null, new Transform(), new Material());
    }

    public Entity(String name)
    {
        init(name, new Transform(), new Material());
    }

    private void init(String name, Transform transform, Material material)
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

    public Entity setScale(float x, float y, float z)
    {
        transform.setScale(x,y,z);
        return this;
    }

    public Entity setAmbient(float r, float g, float b, float a)
    {
        material.setAmbient(r,g,b,a);
        return this;
    }

    public Entity setDiffuse(float r, float g, float b, float a)
    {
        material.setDiffuse(r,g,b,a);
        return this;
    }

    public Entity setReflectance(float reflectance)
    {
        material.setReflectance(reflectance);
        return this;
    }

    public Entity addTexture(Texture texture)
    {
        material.setTexture(texture);
        return this;
    }

    public void setupInspector()
    {
        name = ImGuiLayer.inputText("name", name);
        transform.setupInspector();
        material.setupInspector();
        for (Component component : components.values())
            component.setupInspector();
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

    public Entity setChild(Entity entity){
        this.children.add(entity);
        entity.parent = this;
        return this;
    }

}
