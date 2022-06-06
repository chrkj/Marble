package marble.editor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import imgui.ImGui;
import imgui.type.ImBoolean;

public final class ConsolePanel {

    private static final StringBuffer BUFFER = new StringBuffer();
    private static final ImBoolean SHOULD_SCROLL_TO_BOTTOM = new ImBoolean(false);
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");
    private static final int MAX_LENGTH = 2500;

    public ConsolePanel()
    {
    }

    public void onUpdate()

    {
        ImGui.setNextWindowSize(500, 400);
        ImGui.begin("Console");
        if (ImGui.button("Clear"))
            clear();
        ImGui.separator();
        ImGui.beginChild("scrolling");
        ImGui.textUnformatted(BUFFER.toString());
        if (SHOULD_SCROLL_TO_BOTTOM.get())
            ImGui.setScrollHereY(1f);
        SHOULD_SCROLL_TO_BOTTOM.set(false);
        ImGui.endChild();
        ImGui.end();
    }

    public static void log(String text)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BUFFER.append("[").append(FORMATTER.format(timestamp)).append("]: ").append(text).append("\n");
        if(BUFFER.length() > MAX_LENGTH)
            BUFFER.delete(0, BUFFER.length() - MAX_LENGTH);
    }

    private static void clear()
    {
        BUFFER.delete(0, BUFFER.length());
    }

}
