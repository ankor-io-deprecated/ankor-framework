package at.irian.ankor.context;

import at.irian.ankor.event.ArrayListEventListeners;
import at.irian.ankor.event.EventListeners;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelHolder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolder.class);

    private final Class<?> modelType;
    private final EventListeners modelEventListeners;
    private Object model;

    protected ModelHolder(Class<?> modelType, EventListeners modelEventListeners) {
        this.modelType = modelType;
        this.modelEventListeners = modelEventListeners;
        this.model = null;
    }

    public static ModelHolder create(Class<?> modelType) {
        return new ModelHolder(modelType, new ArrayListEventListeners());
    }

    public Class<?> getModelType() {
        return modelType;
    }

    public EventListeners getModelEventListeners() {
        return modelEventListeners;
    }

    @SuppressWarnings("unchecked")
    public <T> T getModel() {
        return (T)model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
