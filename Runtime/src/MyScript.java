import marble.entity.components.ScriptableComponent;

public class MyScript extends ScriptableComponent
{
    @Override
    public void onInit()
    {

    }

    @Override
    public void onUpdate(float dt)
    {
        float speed = 80 * dt;
        entity.transform.incrementRotation(speed, speed, speed);
    }

}
