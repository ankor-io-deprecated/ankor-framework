package at.irian.ankor.event;

import at.irian.ankor.event.source.Source;

import java.util.EventObject;

/**
 * @author Manfred Geiler
 */
public abstract class ModelEvent extends EventObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEvent.class);

    protected ModelEvent(Source source) {
        super(source);
    }

    @Override
    public Source getSource() {
        return (Source)super.getSource();
    }

    public abstract boolean isAppropriateListener(ModelEventListener listener);

    public abstract void processBy(ModelEventListener listener);
}
