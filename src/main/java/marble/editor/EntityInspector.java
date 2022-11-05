package marble.editor;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

import marble.util.Loader;
import marble.gui.MarbleGui;
import marble.entity.Entity;
import marble.entity.components.Component;
import marble.entity.components.RigidBody;
import marble.entity.components.light.LightType;
import marble.entity.components.light.LightFactory;

public class EntityInspector implements Panel
{
    public EntityInspector() { }

    @Override
    public void onImGuiRender()
    {
        ImGui.begin("Inspector");
        Entity selectedEntity = SceneHierarchy.getSelectedEntity();
        if (selectedEntity != null)
        {
            // Entity name field
            selectedEntity.name = MarbleGui.inputText("name", selectedEntity.name);

            // Entity transform field
            renderTransformInspector(selectedEntity);

            // Entity components fields
            for (Component component : selectedEntity.getAllComponents())
                component.renderEntityInspector();

            // Script component fields
            renderScriptInspector(selectedEntity);

            // Entity add components fields
            ImGui.separator();
            if (ImGui.button("Add component"))
                ImGui.openPopup("add_component_popup");
            if (ImGui.beginPopup("add_component_popup"))
            {
                if (ImGui.beginMenu("Mesh"))
                {
                    if (ImGui.menuItem("Cube")) addCubeMesh();
                    ImGui.endMenu();
                }
                if (ImGui.menuItem("Script"))            selectedEntity.scriptName = "None";
                if (ImGui.menuItem("Camera"))            Console.log("NOT IMPLEMENTED");
                if (ImGui.menuItem("Directional light")) addDirLight();
                if (ImGui.menuItem("Spot light"))        addSpotLight();
                if (ImGui.menuItem("Point light"))       addPointLight();
                if (ImGui.menuItem("RigidBody Static"))  addRigidBody(true);
                if (ImGui.menuItem("RigidBody Dynamic")) addRigidBody(false);

                ImGui.endPopup();
            }
        }
        ImGui.end();
    }

    private void addCubeMesh()
    {
        var mesh =  Loader.loadMeshObj("assets/obj/cube.obj");
        SceneHierarchy.getSelectedEntity().addComponent(mesh);
        EditorLayer.currentScene.getRegistry().register(mesh);
    }
    
    private void addRigidBody(boolean isStatic)
    {
        var rb = new RigidBody(SceneHierarchy.getSelectedEntity(), isStatic);
        SceneHierarchy.getSelectedEntity().addComponent(rb);
        EditorLayer.currentScene.getRegistry().register(rb);
        EditorLayer.currentScene.getPhysicsScene().addActor(rb.rigidActor);
    }

    private void addSpotLight()
    {
        var sl = LightFactory.getLight(LightType.SPOT);
        SceneHierarchy.getSelectedEntity().addComponent(sl);
        EditorLayer.currentScene.getRegistry().register(sl);
    }

    private void addPointLight()
    {
        var pl = LightFactory.getLight(LightType.POINT);
        SceneHierarchy.getSelectedEntity().addComponent(pl);
        EditorLayer.currentScene.getRegistry().register(pl);
    }

    private void addDirLight()
    {
        var dl = LightFactory.getLight(LightType.DIRECTIONAL);
        SceneHierarchy.getSelectedEntity().addComponent(dl);
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
