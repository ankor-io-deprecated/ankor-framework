package at.irian.ankor.sample.fx.view;

import java.util.HashMap;
import java.util.Map;

/**
* @author Thomas Spiegl
*/
public class Tabs {

    private Map<String, Tab> tabs;

    public Tabs() {
        tabs = new HashMap<String, Tab>();
    }

    public Tab newTab() {
        String tabId = Integer.toString(tabs.size());
        Tab tab = new Tab(tabId);
        tabs.put(tabId, tab);
        return tab;
    }

    public Tab getTab(String tabId) {
        return tabs.get(tabId);
    }

}
