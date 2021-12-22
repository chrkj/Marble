package marble;

import marble.Components.Component;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private String name;
    private List<Component> components;

    public GameObject(String name)
    {
        this.name = name;
        this.components = new ArrayList<>();
    }

    public void start()
    {
        for (Component component : components) {
            component.start();
        }
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for (Component component : components) {
            if (componentClass.isAssignableFrom(component.getClass())) {
                return componentClass.cast(component);
            }
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
        for (Component component : components) {
            component.update(dt);
        }
    }

}
