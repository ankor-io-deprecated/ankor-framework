package at.irian.ankor.context;

import at.irian.ankor.event.ListenersHolder;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelHolder {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolder.class);

    private final Class<?> modelType;
    private final ListenersHolder listenersHolder;
    private Object model;

    public ModelHolder(Class<?> modelType, ListenersHolder listenersHolder) {
        this.modelType = modelType;
        this.listenersHolder = listenersHolder;
        this.model = null;
    }

    public Class<?> getModelType() {
        return modelType;
    }

    public ListenersHolder getListenersHolder() {
        return listenersHolder;
    }

    @SuppressWarnings("unchecked")
    public <T> T getModel() {
        return (T)model;
    }

    public void setModel(Object model) {
        this.model = model;
    }
}
