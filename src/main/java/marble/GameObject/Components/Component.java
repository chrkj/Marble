package Marble.GameObject.Components;

import Marble.GameObject.GameObject;

public abstract class Component {

    protected GameObject gameObject = null;

    public Component()
    {
    }

    public abstract void start();

    public abstract void update(float dt);

    public void setGameObject(GameObject gameObject)
    {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject()
    {
        return this.gameObject;
    }
}
