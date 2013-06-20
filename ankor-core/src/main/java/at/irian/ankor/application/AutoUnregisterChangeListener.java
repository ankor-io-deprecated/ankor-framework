package at.irian.ankor.application;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.NilValue;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AutoUnregisterChangeListener implements ChangeListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AutoUnregisterChangeListener.class);

    private final ListenerRegistry listenerRegistry;

    public AutoUnregisterChangeListener(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    @Override
    public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
        if (isNil(changedProperty.getValue())) {
            listenerRegistry.unregisterAllListenersFor(changedProperty);
        }
    }

    private boolean isNil(Object newValue) {
        return NilValue.instance().equals(newValue);
    }

}
