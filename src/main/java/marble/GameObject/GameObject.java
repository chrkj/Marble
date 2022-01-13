package marble.gameobject;

import java.util.List;
import java.util.ArrayList;

import marble.gameobject.components.Component;
import marble.renderer.Shader;

public class GameObject {

    public String name;
    private Shader shader;
    public Transform transform;

    private List<Component> components;

    public GameObject(String name)
    {
        init(name, new Transform(), new Shader("assets/shaders/default.glsl"));
    }

    public GameObject(String name, Transform transform)
    {
        init(name, transform, new Shader("assets/shaders/default.glsl"));
    }

    public GameObject(String name, Transform transform, Shader shader)
    {
        init(name, transform, shader);
    }

    public GameObject(String name, Shader shader)
    {
        init(name, new Transform(), shader);
    }

    public void init(String name, Transform transform, Shader shader)
    {
        this.name = name;
        this.shader = shader;
        this.transform = transform;
        this.components = new ArrayList<>();
        shader.compile();
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

    public Shader getShader()
    {
        return shader;
    }
}
