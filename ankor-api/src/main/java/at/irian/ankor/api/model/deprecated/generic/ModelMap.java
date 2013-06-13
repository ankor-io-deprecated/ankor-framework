package at.irian.ankor.api.model.deprecated.generic;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ModelMap extends ModelObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelMap.class);

    private final Map<String, ModelObject> map = new HashMap<String, ModelObject>();

    public ModelObject put(String key, ModelObject value) {
        return nullSafe(map.put(key, value));
    }

    public ModelObject get(String key) {
        return nullSafe(map.get(key));
    }

    public ModelObject remove(String key) {
        return nullSafe(map.remove(key));
    }
}
