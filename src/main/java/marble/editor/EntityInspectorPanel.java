package marble.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.entity.Entity;
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

            // Entity components fields
            for (Component component : selectedEntity.components.values())
                component.setupInspector();

            // Entity add components fields
            ImGui.separator();
            if (ImGui.button("Add component"))
                ImGui.openPopup("add_component_popup");
            if (ImGui.beginPopup("add_component_popup"))
            {
                if (ImGui.selectable("Example component"))
                {
                    // TODO: Add component functionality
                }
                ImGui.endPopup();
            }
        }
        ImGui.end();
    }
}
