package marble.entity.components;

import marble.entity.Entity;

public abstract class ScriptableComponent {

    public transient Entity entity;

    public abstract void onInit();
    public abstract void onUpdate(float dt);
    public abstract void onFixedUpdate(float dt);

}
