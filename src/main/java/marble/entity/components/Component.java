package marble.entity.components;

import com.fasterxml.jackson.annotation.JsonIgnore;

import marble.entity.Entity;

public abstract class Component {

    protected transient Entity entity = null;

    public abstract void cleanUp();
    public abstract void setupInspector();

    public void setEntity(Entity entity)
    {
        this.entity = entity;
    }

    public Entity getEntity()
    {
        return this.entity;
    }
}
