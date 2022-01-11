import marble.ImGui.ImGuiLayer;
import marble.Window;

public class Main {
    public static void main(String[] args)
    {
        Window window = new Window("Window_1", new ImGuiLayer());
        window.run();
        window.destroy();
    }
}
