package marble.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.scene.Scene;
import marble.entity.Entity;

public class SceneHierarchyPanel {

    public Entity selectedEntity;

    public SceneHierarchyPanel()
    {
    }

    public void onUpdate(Scene currentScene)
    {
        ImGui.begin("Hierarchy");
        int nodeFlags = ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        for (Entity entity : currentScene.getEntities())
            recursiveDrawCall(entity, nodeFlags);
        ImGui.end();
    }

    private void recursiveDrawCall(Entity entity, int nodeFlags)
    {
        int currentFlags = nodeFlags;
        if (entity.getChildren().size() == 0) currentFlags |= ImGuiTreeNodeFlags.Leaf;
        if (entity == selectedEntity)         currentFlags |= ImGuiTreeNodeFlags.Selected;

        boolean nodeOpen;
        if (entity.name.length() == 0)
            nodeOpen = ImGui.treeNodeEx("##" + entity.name, currentFlags);
        else
            nodeOpen = ImGui.treeNodeEx(entity.name, currentFlags);

        if (ImGui.isItemClicked())
            selectedEntity = entity;

        if (nodeOpen)
        {
            for (Entity child : entity.getChildren())
                recursiveDrawCall(child, nodeFlags);
            ImGui.treePop();
        }
    }

}
