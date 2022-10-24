package marble.editor;

import imgui.ImGui;
import imgui.extension.imguizmo.ImGuizmo;
import imgui.extension.imguizmo.flag.Mode;
import imgui.extension.imguizmo.flag.Operation;

import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Gizmo
{
    private static int currentGizmoOperation = Operation.TRANSLATE;
    private static final int CAM_DISTANCE = 8;
    private static final float[] proj = new float[16];
    private static final float[] view = new float[16];
    private static final float[] VIEW_MANIPULATE_SIZE = new float[]{ 128f, 128f };

    public static void onImGuiRender()
    {
        EditorLayer.currentScene.editorCamera.getViewMatrix().get(view);
        EditorLayer.currentScene.editorCamera.getProjectionMatrixEditor().get(proj);

        float windowWidth = ImGui.getWindowWidth();
        float windowHeight = ImGui.getWindowHeight();
        float viewManipulateRight = ImGui.getWindowPosX() + windowWidth;
        float viewManipulateTop = ImGui.getWindowPosY();

        // TODO: Fix view manipulation
        ImGuizmo.viewManipulate(view, CAM_DISTANCE, new float[]{viewManipulateRight - 128, viewManipulateTop}, VIEW_MANIPULATE_SIZE, 0x10101010);

        var selectedEntity = SceneHierarchy.getSelectedEntity();
        if (selectedEntity == null) return;

        if (ImGui.isKeyPressed(GLFW_KEY_T))
            currentGizmoOperation = Operation.TRANSLATE;
        else if (ImGui.isKeyPressed(GLFW_KEY_R))
            currentGizmoOperation = Operation.ROTATE;
        else if (ImGui.isKeyPressed(GLFW_KEY_S))
            currentGizmoOperation = Operation.SCALE;

        ImGuizmo.setRect(ImGui.getWindowPosX(), ImGui.getWindowPosY(), windowWidth, windowHeight);
        ImGuizmo.setOrthographic(false);
        ImGuizmo.setEnabled(true);
        ImGuizmo.setDrawList();

        float[] modelMatrix = new float[16];
        float[] pos = { selectedEntity.transform.getPosition().x, selectedEntity.transform.getPosition().y, selectedEntity.transform.getPosition().z };
        float[] rot = { selectedEntity.transform.getRotation().x, selectedEntity.transform.getRotation().y, selectedEntity.transform.getRotation().z };
        float[] scale = { selectedEntity.transform.getScale().x, selectedEntity.transform.getScale().y, selectedEntity.transform.getScale().z };
        ImGuizmo.recomposeMatrixFromComponents(modelMatrix, pos, rot, scale);

        ImGuizmo.manipulate(view, proj, modelMatrix, currentGizmoOperation, Mode.LOCAL);

        ImGuizmo.decomposeMatrixToComponents(modelMatrix, pos, rot, scale);
        selectedEntity.transform.setPosition(new Vector3f(pos[0], pos[1], pos[2]));
        selectedEntity.transform.setRotation( new Vector3f(rot[0], rot[1], rot[2]));
        selectedEntity.transform.setScale(new Vector3f(scale[0], scale[1], scale[2]));
    }

    public static boolean inUse()
    {
        if (SceneHierarchy.getSelectedEntity() == null)
            return false;
        return ImGuizmo.isUsing();
    }
}
