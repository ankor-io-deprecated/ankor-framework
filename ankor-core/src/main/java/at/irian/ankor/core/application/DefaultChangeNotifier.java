package at.irian.ankor.core.application;

import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.listener.ModelChangeListenerInstance;
import at.irian.ankor.core.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class DefaultChangeNotifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultChangeNotifier.class);

    private final ListenerRegistry listenerRegistry;

    public DefaultChangeNotifier(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void notifyLocalListeners(Ref changedRef) {
        for (ModelChangeListenerInstance changeListenerInstance : listenerRegistry.getLocalChangeListenersFor(changedRef)) {
            ModelChangeListener listener = changeListenerInstance.getListener();
            Ref watchedRef = changeListenerInstance.getRef();
            listener.handleModelChange(watchedRef, changedRef);
        }
    }

}


