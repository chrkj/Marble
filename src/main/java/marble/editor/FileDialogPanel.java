package marble.editor;

import java.util.Map;

import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;

public class FileDialogPanel implements Panel
{
    private Map<String, String> selection = null;

    public String getSelectedFilePath()
    {
        String filePath = null;
        if (selection.values().stream().findFirst().isPresent())
            filePath = selection.values().stream().findFirst().get();
        selection = null;
        return filePath;
    }

    public boolean isFileSelected()
    {
        return selection != null && !selection.isEmpty();
    }

    public void open()
    {
        String sceneFolder = "assets/scenes";
        ImGuiFileDialog.openModal("browse-key", "Choose File", ".marble", sceneFolder, "",null, 250, 1, 42, ImGuiFileDialogFlags.None);
    }

    @Override
    public void onImGuiRender()
    {
        if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600))
        {
            if (ImGuiFileDialog.isOk())
                selection = ImGuiFileDialog.getSelection();
            ImGuiFileDialog.close();
        }
    }
}
