import Marble.Window;

public class Main {
    public static void main(String[] args)
    {
        Window window = new Window("Marble");
        window.init();
        window.run();
        window.destroy();
    }
}
