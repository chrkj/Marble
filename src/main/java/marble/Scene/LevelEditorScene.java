package marble.Scene;

import marble.Camera.Camera;
import marble.Components.FontRenderer;
import marble.Components.SpriteRenderer;
import marble.GameObject;
import marble.Listeners.KeyListener;
import marble.Window;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import util.Time;

import java.awt.event.KeyEvent;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;
    private float camSpeed = 50.0f;
    private int vaoID, vboID, eboID;
    private Shader defaultShader;

    private float[] vertexArray = {
            // Position              // Color
             100.5f, 0.5f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f, // Bottom right
            -0.5f,   100.5f, 0.0f,   0.0f, 1.0f, 0.0f, 1.0f, // Top left
             100.5f, 100.5f, 0.0f,   1.0f, 0.0f, 1.0f, 1.0f, // Top right
            -0.5f,  -0.5f, 0.0f,     0.0f, 0.0f, 0.0f, 1.0f // Bottom left
    };
    private int[] elementArray = {
            2, 1, 0,
            0, 1, 3
    };

    public LevelEditorScene()
    {
        System.out.println("Inside level editor scene.");
    }

    @Override
    public void init()
    {
        GameObject testObj = new GameObject("testObj");
        testObj.addComponent(new FontRenderer());
        addGameObjectToScene(testObj);

        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/default.shader");
        defaultShader.compile();

        // Generate VAO, VBO and EBO
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = Float.BYTES;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes,0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt)
    {
        if (KeyListener.isKeyPressed(KeyEvent.VK_W))
            camera.position.y -= dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_S))
            camera.position.y += dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_A))
            camera.position.x += dt * camSpeed;
        if (KeyListener.isKeyPressed(KeyEvent.VK_D))
            camera.position.x -= dt * camSpeed;

        defaultShader.use();
        defaultShader.uploadFloat("uTime", Time.getTime());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());

        // Bind VAO
        glBindVertexArray(vaoID);

        // Enable vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        defaultShader.detach();

        if (!changingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE))
            changingScene = true;

        if (changingScene && timeToChangeScene > 0)
            timeToChangeScene -= dt;
        else if (changingScene)
            Window.changeScene(1);

        for (GameObject gameObject : gameObjects)
            gameObject.update(dt);
    }
}
