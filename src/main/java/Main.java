import marble.Window;

public class Main {
    public static void main(String[] args) throws Exception
    {
        Window window = new Window("marble");
        window.init();
        window.run();
        window.destroy();
    }
}
