package marble.editor;

import marble.entity.Entity;
import marble.entity.components.camera.EditorCamera;
import org.joml.*;

import java.util.List;

public class MousePicker {

    private static final Vector3f max = new Vector3f();
    private static final Vector3f min = new Vector3f();
    private static final Vector4f tmpVec = new Vector4f();
    private static final Vector2f nearFar = new Vector2f();
    private static final Vector3f mouseDir = new Vector3f();
    private static final Matrix4f invViewMatrix = new Matrix4f();
    private static final Matrix4f invProjectionMatrix = new Matrix4f();


    public MousePicker()
    {
    }

    public static Entity selectGameItem(List<Entity> gameItems, Vector2d mousePos, EditorCamera camera) {
        // Transform mouse coordinates into normalized spaze [-1, 1]
        int wdwWitdh = (int)EditorLayer.editorViewportSize.x;
        int wdwHeight = (int)EditorLayer.editorViewportSize.y;

        float x = (float)(2 * mousePos.x) / (float)wdwWitdh - 1.0f;
        float y = 1.0f - (float)(2 * mousePos.y) / (float)wdwHeight;
        float z = -1.0f;

        invProjectionMatrix.set(camera.getProjectionMatrixEditor());
        invProjectionMatrix.invert();

        tmpVec.set(x, y, z, 1.0f);
        tmpVec.mul(invProjectionMatrix);
        tmpVec.z = -1.0f;
        tmpVec.w = 0.0f;

        Matrix4f viewMatrix = camera.getViewMatrix();
        invViewMatrix.set(viewMatrix);
        invViewMatrix.invert();
        tmpVec.mul(invViewMatrix);

        mouseDir.set(tmpVec.x, tmpVec.y, tmpVec.z);

        Entity selectedGameItem = null;
        float closestDistance = Float.POSITIVE_INFINITY;

        for (Entity gameItem : gameItems)
        {
            min.set(gameItem.transform.position);
            max.set(gameItem.transform.position);
            min.add(-gameItem.transform.scale.x, -gameItem.transform.scale.y, -gameItem.transform.scale.z);
            max.add(gameItem.transform.scale.x, gameItem.transform.scale.y, gameItem.transform.scale.z);
            if (Intersectionf.intersectRayAab(camera.position, mouseDir, min, max, nearFar) && nearFar.x < closestDistance)
            {
                closestDistance = nearFar.x;
                selectedGameItem = gameItem;
            }
        }
        // TODO: Fix targeting
        return selectedGameItem;
    }

}
