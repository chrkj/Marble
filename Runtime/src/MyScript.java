import marble.editor.ConsolePanel;
import marble.entity.components.ScriptableComponent;

public class MyScript extends ScriptableComponent {

    private int speed = 50;

    @Override
    public void onInit()
    {
        ConsolePanel.log("init");
    }

    @Override
    public void onUpdate(float dt)
    {
        entity.transform.incrementRotation(dt * speed, dt * speed, dt * speed);
    }

    @Override
    public void onFixedUpdate(float dt)
    {
        // NOT IMPLEMENTED
    }
}
