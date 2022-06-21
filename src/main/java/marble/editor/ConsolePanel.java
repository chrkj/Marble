package marble.editor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import imgui.ImGui;
import imgui.type.ImBoolean;

public final class ConsolePanel implements Panel {

    private static final int MAX_LENGTH = 100000;
    private static final StringBuffer BUFFER = new StringBuffer();
    private static final ImBoolean SHOULD_SCROLL_TO_BOTTOM = new ImBoolean(false);
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");

    public ConsolePanel() { }

    public static void log(String text)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BUFFER.append("[").append(FORMATTER.format(timestamp)).append("]: ").append(text).append("\n");
        if(BUFFER.length() > MAX_LENGTH)
            BUFFER.delete(0, BUFFER.length() - MAX_LENGTH);
    }

    public static void log(float text)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BUFFER.append("[").append(FORMATTER.format(timestamp)).append("]: ").append(text).append("\n");
        if(BUFFER.length() > MAX_LENGTH)
            BUFFER.delete(0, BUFFER.length() - MAX_LENGTH);
    }

    public static void log(boolean bool)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        BUFFER.append("[").append(FORMATTER.format(timestamp)).append("]: ").append(bool).append("\n");
        if(BUFFER.length() > MAX_LENGTH)
            BUFFER.delete(0, BUFFER.length() - MAX_LENGTH);
    }

    @Override
    public void onImGuiRender()
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
        SHOULD_SCROLL_TO_BOTTOM.set(true);
        ImGui.endChild();
        ImGui.end();
    }

    private static void clear()
    {
        BUFFER.delete(0, BUFFER.length());
    }

}
