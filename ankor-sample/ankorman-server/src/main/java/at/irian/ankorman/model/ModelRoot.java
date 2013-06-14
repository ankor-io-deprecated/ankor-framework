package at.irian.ankorman.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelRoot {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelRoot.class);

    private final Menu menu = new Menu();
    private final Map<String, Object> workspaces = new HashMap<String, Object>();

    public Menu getMenu() {
        return menu;
    }

    public Map<String, Object> getWorkspaces() {
        return workspaces;
    }

}
