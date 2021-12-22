package marble.Components;

public class SpriteRenderer extends Component {

    private boolean firstTime = true;

    @Override
    public void start() {
        System.out.println("Spriterender starting!");
    }

    @Override
    public void update(float dt) {
        if (firstTime) {
            System.out.println("Spriterenderer updating!");
            firstTime = false;
        }
    }
}
