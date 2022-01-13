package marble.gameobject;

import java.util.List;
import java.util.ArrayList;

import org.joml.Vector3f;

import marble.gameobject.components.Component;

public class GameObject {

    public String name;
    public Transform transform;

    public float imGuiOffsetScale = 0;
    public Vector3f imGuiOffsetPos = new Vector3f();
    public Vector3f imGuiOffsetRot = new Vector3f();

    private List<Component> components;

    public GameObject(String name)
    {
        init(name, new Transform());
    }

    public GameObject(String name, Transform transform)
    {
        init(name, transform);
    }

    public void init(String name, Transform transform)
    {
        this.name = name;
        this.transform = transform;
        this.components = new ArrayList<>();
    }

    public void start()
    {
        for (Component component : components)
            component.start();
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass()))
                return componentClass.cast(component);
        }
        return null;
    }

    public <T extends Component> T removeComponent(Class<T> componentClass)
    {
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (componentClass.isAssignableFrom(component.getClass())) {
                components.remove(i);
                return componentClass.cast(component);
            }
        }
        return null;
    }

    public void addComponent(Component component)
    {
        components.add(component);
        component.setGameObject(this);
    }

    public void update(float dt)
    {
        for (Component component : components)
            component.update(dt);
    }

    public void render()
    {
        for (Component component : components)
            component.render();
    }

    public boolean hasComponent(Class<? extends Component> componentClass)
    {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass()))
                return true;
        }
        return false;
    }

    public List<Component> getAllComponents()
    {
        return components;
    }
}
