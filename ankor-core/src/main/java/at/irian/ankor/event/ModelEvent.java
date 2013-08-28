package at.irian.ankor.event;

import java.util.EventObject;

/**
 * @author Manfred Geiler
 */
public abstract class ModelEvent extends EventObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEvent.class);

    public ModelEvent(Object source) {
        super(source);
    }

    public abstract boolean isAppropriateListener(ModelEventListener listener);

    public abstract void processBy(ModelEventListener listener);
}
