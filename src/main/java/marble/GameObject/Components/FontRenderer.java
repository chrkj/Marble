package marble.GameObject.Components;

public class FontRenderer extends Component {

    @Override
    public void start()
    {
        if (gameObject.getComponent(SpriteRenderer.class) != null)
            System.out.println("Found font marble.renderer");
    }

    @Override
    public void update(float dt)
    {

    }
}
