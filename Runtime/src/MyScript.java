import marble.editor.ConsolePanel;
import marble.entity.components.ScriptableComponent;

public class MyScript extends ScriptableComponent {

    private boolean oneShot = true;

    @Override
    public void onInit()
    {
        ConsolePanel.log("init");
    }

    @Override
    public void onUpdate(float dt)
    {
        if (oneShot)
        {
            ConsolePanel.log(entity.name);
            entity.setScale(2,2,2);
            oneShot = false;
        }
    }

    @Override
    public void onFixedUpdate(float dt)
    {

    }
}
