package at.irian.ankor.sample.fx.view.model;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
* @author Thomas Spiegl
*/
public class Tabs extends AbstractMap<String, Tab> {

    private Map<String, Tab> tabs;

    public Tabs() {
        tabs = new HashMap<String, Tab>();
    }

    @Override
    public Set<Entry<String, Tab>> entrySet() {
        return tabs.entrySet();
    }

    @Override
    public Tab put(String key, Tab value) {
        if (value == null) {
            return tabs.remove(key);
        } else {
            return tabs.put(key, value);
        }
    }
}
