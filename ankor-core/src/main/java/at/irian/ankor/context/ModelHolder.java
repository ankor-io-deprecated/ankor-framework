package at.irian.ankor.context;

import at.irian.ankor.event.EventBus;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelHolder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolder.class);

    private final Class<?> modelType;
    private final EventBus eventBus;
    private Object model;

    public ModelHolder(Class<?> modelType, EventBus eventBus) {
        this.modelType = modelType;
        this.eventBus = eventBus;
        this.model = null;
    }

    public Class<?> getModelType() {
        return modelType;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    @SuppressWarnings("unchecked")
    public <T> T getModel() {
        return (T)model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
