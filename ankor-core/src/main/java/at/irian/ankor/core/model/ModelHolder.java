package at.irian.ankor.core.model;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelHolder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolder.class);

    private final Class<?> modelType;
    private Object model;

    public ModelHolder(Class<?> modelType) {
        this.modelType = modelType;
        this.model = null;
    }

    public Class<?> getModelType() {
        return modelType;
    }

    @SuppressWarnings("unchecked")
    public <T> T getModel() {
        return (T)model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
