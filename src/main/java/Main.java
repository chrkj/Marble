import marble.Window;
import org.joml.Vector3f;

public class Main {
    public static void main(String[] args) {
        Window window = new Window("Marble");
        window.init();
        window.run();
        window.destroy();

        //float yaw = 45f;
        //float pitch = -45f;
        //Vector3f vec = new Vector3f(0,0,-1);
        //float x = (float) (-Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)));
        //float y = (float) Math.sin(Math.toRadians(pitch));
        //float z = (float) (-Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)));
        //System.out.println("x: " + x + " y: " + y + " z: " + z);
    }
}
