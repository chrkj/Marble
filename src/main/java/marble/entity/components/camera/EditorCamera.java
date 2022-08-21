package marble.entity.components.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;

import marble.Application;
import marble.editor.EditorLayer;
import marble.listeners.KeyListener;
import marble.listeners.MouseListener;

public class EditorCamera extends Camera {

    public final Vector3f position = new Vector3f(-2.5f, -15, -70);
    public final Vector3f rotation = new Vector3f(22, 0, 0);

    public float near = 0.3f;
    public float far = 1000.f;
    public float fov = (float) Math.toRadians(60.0f);

    public void move(float x, float y, float z)
    {
        if (z != 0)
        {
            position.x += Math.sin(Math.toRadians(rotation.y)) * -z * Math.cos(Math.toRadians(rotation.x));
            position.z += Math.cos(Math.toRadians(rotation.y)) * z * Math.cos(Math.toRadians(rotation.x));
            position.y += Math.sin(Math.toRadians(rotation.x)) * z;
        }
        if (x != 0)
        {
            position.x += Math.sin(Math.toRadians(rotation.y - 90)) * -x;
            position.z += Math.cos(Math.toRadians(rotation.y - 90)) * x;
        }
        position.y += y;
    }

    public void rotate(float x, float y, float z)
    {
        rotation.x += x;
        rotation.y += y;
        rotation.z += z;

        if (rotation.x > 90)
            rotation.x = 90;
        else if (rotation.x < -90)
            rotation.x = -90;
    }

    public void onUpdate(float dt)
    {
        if (EditorLayer.inputFlag)
        {
            float camSpeed = 10 * dt;
            float camRotSpeed = 15 * dt;

            glfwSetInputMode(Application.windowPtr, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
            rotate(-MouseListener.mouseDelta().x * camRotSpeed, -MouseListener.mouseDelta().y * camRotSpeed, 0);

            if (KeyListener.isKeyPressed(GLFW_KEY_W))
                move(0, 0, camSpeed);
            if (KeyListener.isKeyPressed(GLFW_KEY_S))
                move(0, 0, -camSpeed);
            if (KeyListener.isKeyPressed(GLFW_KEY_A))
                move(camSpeed, 0, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_D))
                move(-camSpeed, 0, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_E))
                move(0, camSpeed, 0);
            if (KeyListener.isKeyPressed(GLFW_KEY_Q))
                move(0, -camSpeed, 0);
        }
        else
        {
            glfwSetInputMode(Application.windowPtr, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        }
    }

    @Override
    public Matrix4f getViewMatrix()
    {
        var view = new Matrix4f().identity();
        view.rotate((float) Math.toRadians(rotation.x), new Vector3f(1, 0, 0))
                .rotate((float) Math.toRadians(rotation.y), new Vector3f(0, 1, 0))
                .rotate((float) Math.toRadians(rotation.z), new Vector3f(0, 0, 1));
        view.translate(position.x, position.y, position.z);
        return view;
    }

    @Override
    public Matrix4f getProjectionMatrixGame()
    {
        var proj = new Matrix4f().identity();
        float aspectRatio = EditorLayer.gameViewportSize.x / EditorLayer.gameViewportSize.y;
        return proj.setPerspective(fov, aspectRatio, near, far);
    }

    @Override
    public Matrix4f getProjectionMatrixEditor()
    {
        float aspectRatio = EditorLayer.editorViewportSize.x / EditorLayer.editorViewportSize.y;
        return projectionMatrix.setPerspective(fov, aspectRatio, near, far);
    }

    @Override
    public void cleanUp()
    {
    }

    @Override
    public void renderEntityInspector()
    {
    }
}
