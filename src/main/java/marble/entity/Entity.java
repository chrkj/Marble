package marble.entity;

import java.util.*;

import imgui.ImGui;
import marble.imgui.ImGuiLayer;
import marble.entity.components.Component;

public class Entity {

    public String name;
    public Transform transform;

    private Entity parent = null;
    private final List<Entity> children = new ArrayList<>();
    private final Map<Class<? extends Component>, Component> components = new HashMap<>();

    public Entity()
    {
        init("Entity", new Transform());
    }

    public Entity(String name)
    {
        init(name, new Transform());
    }

    private void init(String name, Transform transform)
    {
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
        return componentClass.cast(components.remove(componentClass));
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

    public void setupInspector()
    {
        name = ImGuiLayer.inputText("name", name);
        transform.setupInspector();
        for (Component component : components.values())
            component.setupInspector();
        ImGui.separator();
        if (ImGui.button("Add component"))
            ImGui.openPopup("add_component_popup");
        if (ImGui.beginPopup("add_component_popup")) {
            if (ImGui.selectable("Example component")) {
                // TODO: Add functionality
            }
            ImGui.endPopup();
        }
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
