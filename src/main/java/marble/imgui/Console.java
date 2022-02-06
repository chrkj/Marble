package marble.imgui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Console {

    private static final StringBuffer buffer = new StringBuffer();
    private static final SimpleDateFormat sdf3 = new SimpleDateFormat("HH:mm:ss.SSS");

    private Console() { }

    public static void log(String s)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        buffer.append("[" + sdf3.format(timestamp) + "]: ").append(s).append("\n");
    }

    public static void clear()
    {
        buffer.delete(0, buffer.length());
    }

    public static StringBuffer getBuffer()
    {
        return buffer;
    }
}
