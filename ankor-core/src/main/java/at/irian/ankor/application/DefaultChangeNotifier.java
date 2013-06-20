package at.irian.ankor.application;

import at.irian.ankor.change.BoundChangeListener;
import at.irian.ankor.change.ChangeListener;
import at.irian.ankor.application.ListenerRegistry;
import at.irian.ankor.ref.Ref;

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
            Ref watchedRef = boundChangeListener.getWatchedRef();
            listener.processChange(contextRef, watchedRef, changedRef);
        }
    }

}


