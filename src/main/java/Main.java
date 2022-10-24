import marble.Application;

import static java.lang.Math.acos;
import static java.lang.Math.sqrt;

public class Main
{
    public static void main(String[] args)
    {

        //var x = -0.003;
        //var y = 0.014;
        //var z = 0.284;
        //var w = 0.959;
        //var theta = sqrt(1 - (0.7071068 * 0.7071068));
        //var angle = Math.toDegrees(2 * acos(w));
        //System.out.println(Math.toDegrees(2 * acos(w)));
        //System.out.println("x " + x / theta * angle);
        //System.out.println("y " + y / theta * angle);
        //System.out.println("z " + z / theta * angle);

        Application application = new Application("Marble");
        application.init();
        application.run();
        application.destroy();
    }
}
