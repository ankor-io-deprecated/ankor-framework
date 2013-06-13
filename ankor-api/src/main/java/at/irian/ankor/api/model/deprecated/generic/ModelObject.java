package at.irian.ankor.api.model.deprecated.generic;

/**
 */
public abstract class ModelObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelMap.class);

    protected ModelObject nullSafe(ModelObject v) {
        return v != null ? v : ModelValue.NULL;
    }

}
