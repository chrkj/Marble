package marble.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.entity.Entity;
import marble.entity.components.light.LightFactory;
import marble.entity.components.light.LightType;
import marble.gui.MarbleGui;
import marble.entity.components.Component;

public class EntityInspectorPanel implements Panel {

    public EntityInspectorPanel()
    {
    }

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
                if (ImGui.selectable("Script"))            selectedEntity.setScript("MyScript");
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
            MarbleGui.vec3Controller("Position", tc.position);
            MarbleGui.vec3Controller("Rotation", tc.rotation);
            MarbleGui.vec3Controller("Scale", tc.scale);
            ImGui.treePop();
        }
    }

    private void renderScriptInspector(Entity selectedEntity)
    {
        if (selectedEntity.script != null)
        {
            int nodeFlags = ImGuiTreeNodeFlags.Selected | ImGuiTreeNodeFlags.FramePadding | ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.SpanAvailWidth;
            boolean nodeOpen = ImGui.treeNodeEx("Script", nodeFlags);
            if (nodeOpen) {
                MarbleGui.inputText("Script", selectedEntity.script.getClass().getName() + ".java");
                if (ImGui.button("Recompile")) selectedEntity.setScript(selectedEntity.script.getClass().toString());
                ImGui.treePop();
            }
        }
    }
}
