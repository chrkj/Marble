package marble.Scene;

import marble.Camera.Camera;
import marble.Components.SpriteRenderer;
import marble.GameObject;
import marble.Listeners.KeyListener;
import marble.Transform;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {

    // private float camSpeed = 40f;

    public LevelEditorScene()
    {
        System.out.println("Inside level editor scene.");
    }

    @Override
    public void init()
    {
        camera = new Camera(new Vector2f());

        int xOffset = 10;
        int yOffset = 10;
        float totalWidth = (float) (600 - xOffset * 2);
        float totalHeight = (float) (300 - yOffset * 2);
        float sizeX = totalWidth / 100f;
        float sizeY = totalHeight / 100f;

        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                float xPos = xOffset + (x * sizeX);
                float yPos = yOffset + (y * sizeY);
                GameObject gameObject = new GameObject("Obj " + x + ", " + y, new Transform(new Vector2f(xPos, yPos), new Vector2f(sizeX, sizeY)));
                gameObject.addComponent(new SpriteRenderer(new Vector4f(xPos / totalWidth, yPos / totalHeight, 1, 1)));
                addGameObjectToScene(gameObject);
            }
        }
        System.out.println();
    }

    @Override
    public void update(float dt)
    {
       // if (KeyListener.isKeyPressed(KeyEvent.VK_W))
       //     camera.position.y -= dt * camSpeed;
       // if (KeyListener.isKeyPressed(KeyEvent.VK_S))
       //     camera.position.y += dt * camSpeed;
       // if (KeyListener.isKeyPressed(KeyEvent.VK_A))
       //     camera.position.x += dt * camSpeed;
       // if (KeyListener.isKeyPressed(KeyEvent.VK_D))
       //     camera.position.x -= dt * camSpeed;

        for (GameObject gameObject : gameObjects) {
            gameObject.update(dt);
        }

        renderer.render();
        //System.out.println("FPS: " + 1/dt);
    }
}
