package Marble.GameObject;

import Marble.GameObject.Components.Component;
import org.joml.Vector3f;

import java.util.List;
import java.util.ArrayList;

public class GameObject {

    private Transform transform;

    private String name;
    private List<Component> components;
    public float[] translationX = new float[]{0};
    public float[] translationY = new float[]{0};
    public float[] translationZ = new float[]{0};

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

    public String getName()
    {
        return name;
    }

    public Transform getTransform()
    {
        return transform;
    }

    public void translate(float[] x, float[] y, float[] z)
    {
        translationX = x;
        translationY = y;
        translationZ = z;
    }

    public Vector3f getPosition()
    {
        return new Vector3f(transform.position.x + translationX[0], transform.position.y + translationY[0], transform.position.z + translationZ[0]);
    }
}
