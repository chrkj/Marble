package marble.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.gui.MarbleGui;
import marble.entity.Entity;
import marble.entity.components.Component;
import marble.entity.components.light.LightFactory;
import marble.entity.components.light.LightType;

public class EntityInspectorPanel implements Panel
{
    public EntityInspectorPanel() { }

    @Override
    public void onImGuiRender()
    {
        ImGui.begin("Inspector");
        Entity selectedEntity = SceneHierarchyPanel.getSelectedEntity();
        if (selectedEntity != null)
        {
            // Entity name field
            selectedEntity.name = MarbleGui.inputText("name", selectedEntity.name);

            // Entity transform field
            renderTransformInspector(selectedEntity);

            // Entity components fields
            for (Component component : selectedEntity.components.values())
                component.renderEntityInspector();

            // Script component fields
            renderScriptInspector(selectedEntity);

            // Entity add components fields
            ImGui.separator();
            if (ImGui.button("Add component"))
                ImGui.openPopup("add_component_popup");
            if (ImGui.beginPopup("add_component_popup"))
            {
                if (ImGui.selectable("Mesh"))              ConsolePanel.log("NOT IMPLEMENTED");
                if (ImGui.selectable("Script"))            selectedEntity.scriptName = "None";
                if (ImGui.selectable("Camera"))            ConsolePanel.log("NOT IMPLEMENTED");
                if (ImGui.selectable("Directional light")) addDirLight();
                if (ImGui.selectable("Spot light"))        addSpotLight();
                if (ImGui.selectable("Point light"))       addPointLight();
                ImGui.endPopup();
            }
        }
        ImGui.end();
    }

    private void addSpotLight()
    {
        var sl = LightFactory.getLight(LightType.SPOT);
        SceneHierarchyPanel.getSelectedEntity().addComponent(sl);
        EditorLayer.currentScene.getRegistry().register(sl);
    }

    private void addPointLight()
    {
        var pl = LightFactory.getLight(LightType.POINT);
        SceneHierarchyPanel.getSelectedEntity().addComponent(pl);
        EditorLayer.currentScene.getRegistry().register(pl);
    }

    private void addDirLight()
    {
        var dl = LightFactory.getLight(LightType.DIRECTIONAL);
        SceneHierarchyPanel.getSelectedEntity().addComponent(dl);
        EditorLayer.currentScene.getRegistry().register(dl);
    }

    private void renderTransformInspector(Entity selectedEntity)
    {
        var tc = selectedEntity.transform;
        int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
        boolean nodeOpen = ImGui.treeNodeEx("Transform", nodeFlags);
        if (nodeOpen)
        {
            MarbleGui.vec3Controller("Position", tc.getPosition());
            MarbleGui.vec3Controller("Rotation", tc.getRotation());
            MarbleGui.vec3Controller("Scale", tc.getScale());
            ImGui.treePop();
        }
    }

    private void renderScriptInspector(Entity selectedEntity)
    {
        if (selectedEntity.scriptName != null)
        {
            int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
            boolean nodeOpen = ImGui.treeNodeEx("Script", nodeFlags);
            if (nodeOpen) {
                selectedEntity.scriptName = MarbleGui.inputText("Script", selectedEntity.scriptName);

                handleDragDropScript(selectedEntity);

                if (ImGui.button("Recompile"))
                    selectedEntity.setScript(selectedEntity.scriptName);
                ImGui.treePop();
            }
        }
    }

    private void handleDragDropScript(Entity selectedEntity)
    {
        // DragDrop script
        if (ImGui.beginDragDropTarget())
        {
            var payload = ImGui.acceptDragDropPayload("CONTENT_BROWSER_FILE");
            if (payload != null)
            {
                String filePath = payload.toString();
                if (!filePath.endsWith(".java")) return;
                selectedEntity.scriptName = filePath.substring(filePath.lastIndexOf('\\') + 1);
            }
        }
    }
}
