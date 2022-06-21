package marble.editor;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.ImGuiWindowClass;
import imgui.flag.ImGuiWindowFlags;

import marble.util.Loader;
import marble.entity.components.Texture;

public class ToolPanel implements Panel {

    private final int ImGuiDockNodeFlags_NoTabBar = 1 << 12;
    private final Texture playButtonIcon = Loader.loadTexture("assets/textures/PlayButtonIcon.png");
    private final Texture stopButtonIcon = Loader.loadTexture("assets/textures/StopButtonIcon.png");

    @Override
    public void onImGuiRender()
    {
        ImGuiWindowClass windowClass = new ImGuiWindowClass();
        windowClass.addDockNodeFlagsOverrideSet(ImGuiDockNodeFlags_NoTabBar);
        ImGui.setNextWindowClass(windowClass);
        int windowFlags = ImGuiWindowFlags.NoCollapse |  ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoTitleBar
                | ImGuiWindowFlags.NoNavFocus | ImGuiWindowFlags.NoNav | ImGuiWindowFlags.NoDecoration;

        ImGui.begin("Tool Panel", windowFlags);

        ImGuiStyle style = ImGui.getStyle();

        float size = ImGui.getItemRectSizeX() + style.getFramePaddingX() * 2.0f;
        float avail = ImGui.getContentRegionAvail().x;

        float off = (avail - size) * 0.5f;
        if (off > 0.0f)
            ImGui.setCursorPosX(ImGui.getCursorPosX() + off);
        ImGui.setCursorPosY(1);
        Texture icon = EditorLayer.sceneRunning ? stopButtonIcon : playButtonIcon;
        if(ImGui.imageButton(icon.getId(), 34, 34))
        {
            EditorLayer.sceneRunning = !EditorLayer.sceneRunning;
        }

        ImGui.end();
    }

}