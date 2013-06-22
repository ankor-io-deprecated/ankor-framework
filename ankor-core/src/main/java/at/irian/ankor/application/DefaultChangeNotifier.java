package at.irian.ankor.application;

import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.event.ChangeNotifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class DefaultChangeNotifier implements ChangeNotifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultChangeNotifier.class);

    private final ListenerRegistry listenerRegistry;

    public DefaultChangeNotifier(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    @Override
    public void broadcastChange(Ref modelContext, Ref changedProperty) {

        if (changedProperty.isDeleted()) {
            listenerRegistry.unregisterAllListenersFor(changedProperty);
        }

        for (BoundChangeListener boundChangeListener : listenerRegistry.getLocalChangeListenersFor(changedProperty)) {
            ChangeListener listener = boundChangeListener.getListener();
            Ref watchedRef = boundChangeListener.getWatchedRef();
            listener.processChange(modelContext, watchedRef, changedProperty);
        }
    }

}


