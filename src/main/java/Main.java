import Marble.Window;

public class Main {
    public static void main(String[] args) throws Exception
    {
        Window window = new Window("Marble");
        window.init();
        window.run();
        window.destroy();
    }
}
