package marble.renderer;

public class RenderingAPI {

    public enum APIType { None, OPENGL }

    private static APIType currentRenderingAPI;

    public static APIType getCurrent()
    {
        return currentRenderingAPI;
    }

    public static void setAPI(APIType type)
    {
        currentRenderingAPI = type;
    }
}
