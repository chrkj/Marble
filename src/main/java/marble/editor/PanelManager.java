package marble.editor;

import java.util.HashMap;

public class PanelManager {

    private final HashMap<String, Panel> panels = new HashMap<>();

    public PanelManager() { }

    public void addPanel(Panel panel, String panelID)
    {
        panels.put(panelID, panel);
    }

    public void removePanel(String panelID)
    {
        panels.remove(panelID);
    }

    public Panel getPanel(String panelID)
    {
        return panels.get(panelID);
    }

    public void onImGuiRender()
    {
        for (Panel panel : panels.values())
            panel.onImGuiRender();
    }

}
