package at.irian.ankor.api.model.deprecated.generic;

import java.util.ArrayList;

/**
 */
public class ModelArray extends ModelObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelMap.class);

    private final ArrayList<ModelObject> list = new ArrayList<ModelObject>();

    public ModelObject set(int idx, ModelObject value) {
        list.ensureCapacity(idx + 1);
        for (int i = list.size(); i < idx; i++) {
            list.add(null);
        }
        return list.set(idx, value);
    }

    public ModelObject get(int idx) {
        if (idx < list.size()) {
            return nullSafe(list.get(idx));
        } else {
            return ModelValue.NULL;
        }
    }

    public void clear() {
        list.clear();
    }

}
