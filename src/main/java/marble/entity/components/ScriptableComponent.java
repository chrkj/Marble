package marble.entity.components;

import marble.entity.Entity;
import marble.editor.EditorLayer;

public abstract class ScriptableComponent
{
    public Entity entity;

    public abstract void onInit();
    public abstract void onUpdate(float dt);

    protected final Entity getEntity(String name)
    {
        var entities = EditorLayer.currentScene.getEntities();
        for (Entity ent : entities)
        {
            if (ent.name.equals(name))
                return ent;
        }
        return null;
    }

}
