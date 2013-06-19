package at.irian.ankor.core.application;

import at.irian.ankor.core.listener.BoundChangeListener;
import at.irian.ankor.core.listener.ChangeListener;
import at.irian.ankor.core.listener.ListenerRegistry;
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

    public void notifyLocalListeners(Ref contextRef, Ref changedRef) {
        for (BoundChangeListener boundChangeListener : listenerRegistry.getLocalChangeListenersFor(changedRef)) {
            ChangeListener listener = boundChangeListener.getListener();
            Ref watchedRef = boundChangeListener.getRef();
            listener.processChange(contextRef, watchedRef, changedRef);
        }
    }

}


