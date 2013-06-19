package at.irian.ankor.core.listener;

import at.irian.ankor.core.ref.Ref;
import at.irian.ankor.core.util.NilValue;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class AutoUnregisterChangeListener implements ModelChangeListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AutoUnregisterChangeListener.class);

    private final ListenerRegistry listenerRegistry;

    public AutoUnregisterChangeListener(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    @Override
    public void handleModelChange(Ref contextRef, Ref watchedRef, Ref changedRef) {
        if (isNil(changedRef.getValue())) {
            listenerRegistry.unregisterAllListenersFor(changedRef);
        }
    }

    private boolean isNil(Object newValue) {
        return NilValue.instance().equals(newValue);
    }

}
