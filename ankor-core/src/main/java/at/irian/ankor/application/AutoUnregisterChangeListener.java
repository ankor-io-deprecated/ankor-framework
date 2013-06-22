package at.irian.ankor.application;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.util.NilValue;

/**
 * @author MGeiler (Manfred Geiler)
 */
@Deprecated
public class AutoUnregisterChangeListener implements ChangeListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AutoUnregisterChangeListener.class);

    private final ListenerRegistry listenerRegistry;

    public AutoUnregisterChangeListener(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    @Override
    public void processChange(Ref modelContext, Ref watchedProperty, Ref changedProperty) {
        if (changedProperty.isDeleted()) {
            listenerRegistry.unregisterAllListenersFor(changedProperty);
        }
    }

    private boolean isNil(Object newValue) {
        return NilValue.instance().equals(newValue);
    }

}
