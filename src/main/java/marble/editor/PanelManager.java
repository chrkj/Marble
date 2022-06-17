package marble.editor;

import java.util.HashMap;

public class PanelManager {

    private final HashMap<Class<? extends Panel>, Panel> panels = new HashMap<>();

    public PanelManager() { }

    public void addPanel(Panel panel)
    {
        panels.put(panel.getClass(), panel);
    }

    public void removePanel(Class<? extends Panel> panelClass)
    {
        panels.remove(panelClass);
    }

    public Panel getPanel(Class<? extends Panel> panelClass)
    {
        return panels.get(panelClass);
    }

    public void onImGuiRender()
    {
        for (Panel panel : panels.values())
            panel.onImGuiRender();
    }

}
