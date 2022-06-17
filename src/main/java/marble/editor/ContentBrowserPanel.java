package marble.editor;

import java.io.File;
import java.util.Objects;

import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiMouseButton;

import marble.util.Loader;
import marble.entity.components.Texture;

public class ContentBrowserPanel implements Panel {

    private String currentDir;
    private final String assetsDir = "assets";
    private final Texture dirIcon;
    private final Texture fileIcon;
    private final float padding = 15f;
    private final float thumbnailSize = 64;

    public ContentBrowserPanel()
    {
        currentDir = assetsDir;
        dirIcon = Loader.loadTexture("assets/textures/DirIcon.png");
        fileIcon = Loader.loadTexture("assets/textures/FileIcon.png");
    }

    @Override
    public void onImGuiRender()
    {
        ImGui.begin("Content Browser");

        File dir = new File(currentDir);
        if (ImGui.button("<-"))
            currentDir = dir.getAbsoluteFile().getParent();
        ImGui.sameLine();
        ImGui.text(dir.getAbsolutePath());

        float cellSize = thumbnailSize + padding;
        float panelWidth = ImGui.getContentRegionAvail().x;

        int columnCount = (int)(panelWidth / cellSize) > 0 ? (int)(panelWidth / cellSize) : 1;
        ImGui.columns(columnCount, "", false);

        for (File file : Objects.requireNonNull(dir.listFiles()))
        {
            Texture icon = file.isDirectory() ? dirIcon : fileIcon;
            ImGui.imageButton(icon.getId(), thumbnailSize, thumbnailSize);

            // Drag and drop
            if (ImGui.beginDragDropSource())
            {
                ImGui.setDragDropPayload("CONTENT_BROWSER_FILE", file, ImGuiCond.Once);
                ImGui.endDragDropSource();
            }

            if (ImGui.isItemHovered() && ImGui.isMouseDoubleClicked(ImGuiMouseButton.Left))
            {
                if (file.isDirectory())
                    currentDir = file.getAbsolutePath();
            }
            ImGui.text(file.getName());
            ImGui.nextColumn();
        }

        ImGui.columns(1);
        ImGui.end();
    }
}
