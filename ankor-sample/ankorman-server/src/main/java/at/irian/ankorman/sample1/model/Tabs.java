package at.irian.ankorman.sample1.model;

import at.irian.ankor.ref.Ref;

import java.util.HashMap;

/**
 * @author Thomas Spiegl
 */
public class Tabs extends MapViewModelBase<String, Tab> {

    protected Tabs() {
        super(null, new HashMap<String, Tab>());
    }

    protected Tabs(Ref viewModelRef) {
        super(viewModelRef, new HashMap<String, Tab>());
    }

    @Override
    public Tab put(String key, Tab value) {
        if (value == null) {
            return map.remove(key);
        } else {
            return map.put(key, value);
        }
    }
}
