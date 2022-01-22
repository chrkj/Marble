package marble.gameobject.components;

import marble.gameobject.Entity;

public abstract class Component {

    protected Entity entity = null;

    public abstract void render();

    public abstract void cleanUp();

    public void setGameObject(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getGameObject()
    {
        return this.entity;
    }
}
