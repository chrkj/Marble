package marble.editor;

import java.util.Map;

import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;

public class FileDialog {

    private Map<String, String> selection = null;
    private String sceneFolder = "assets/scenes";

    public void onUpdate() {
        if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600))
        {
            if (ImGuiFileDialog.isOk())
                selection = ImGuiFileDialog.getSelection();
            ImGuiFileDialog.close();
        }
    }

    public String getSelectedFilePath()
    {
        var filePath = selection.values().stream().findFirst().get();
        selection = null;
        return filePath;
    }

    public boolean isFileSelected()
    {
        return selection != null && !selection.isEmpty();
    }

    public void open()
    {
        ImGuiFileDialog.openModal("browse-key", "Choose File", ".marble", sceneFolder, "",null, 250, 1, 42, ImGuiFileDialogFlags.None);
    }
}
