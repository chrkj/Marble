import marble.Application;

public class Main
{
    public static void main(String[] args)
    {
        Application application = new Application("Marble");
        application.init();
        application.run();
        application.destroy();
    }
}
