import Marble.ImGui.ImGuiLayer;
import Marble.Window;

public class Main {
    public static void main(String[] args)
    {
        Window window = new Window("Marble", new ImGuiLayer());
        window.run();
        window.destroy();
    }
}
