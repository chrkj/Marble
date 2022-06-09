package marble.editor;

import java.util.Map;

import imgui.extension.imguifiledialog.ImGuiFileDialog;
import imgui.extension.imguifiledialog.flag.ImGuiFileDialogFlags;

public class FileDialog {

    public Map<String, String> selection = null;

    private String sceneFolder = "assets/scenes";

    public void onUpdate() {
        if (ImGuiFileDialog.display("browse-key", ImGuiFileDialogFlags.None, 200, 400, 800, 600))
        {
            if (ImGuiFileDialog.isOk())
                selection = ImGuiFileDialog.getSelection();
            ImGuiFileDialog.close();
        }
    }

    public void open()
    {
        ImGuiFileDialog.openModal("browse-key", "Choose File", ".marble", sceneFolder, "",null, 250, 1, 42, ImGuiFileDialogFlags.None);
    }
}
