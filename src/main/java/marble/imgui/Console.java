package marble.imgui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import imgui.ImGui;
import imgui.type.ImBoolean;

public class Console {

    private static final StringBuffer buffer = new StringBuffer();
    private static final ImBoolean scrollToBottom = new ImBoolean(false);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");

    public Console()
    {
    }

    public void draw()
    {
        ImGui.setNextWindowSize(500, 400);
        ImGui.begin("Console");
        if (ImGui.button("Clear"))
            clear();
        ImGui.separator();
        ImGui.beginChild("scrolling");
        ImGui.textUnformatted(buffer.toString());
        if (scrollToBottom.get())
            ImGui.setScrollHereY(1f);
        scrollToBottom.set(false);
        ImGui.endChild();
        ImGui.end();
    }

    public static void log(String text)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        buffer.append("[").append(formatter.format(timestamp)).append("]: ").append(text).append("\n");
    }

    private void clear()
    {
        buffer.delete(0, buffer.length());
    }

}
