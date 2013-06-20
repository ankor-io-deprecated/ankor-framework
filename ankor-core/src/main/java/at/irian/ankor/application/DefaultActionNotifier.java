package at.irian.ankor.application;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.event.ActionNotifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class DefaultActionNotifier implements ActionNotifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultActionNotifier.class);

    private final ListenerRegistry listenerRegistry;

    public DefaultActionNotifier(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    @Override
    public void broadcastAction(Ref modelContext, Action action) {
        if (modelContext == null) {
            throw new NullPointerException("contextRef");
        }
        if (action == null) {
            throw new NullPointerException("action");
        }
        for (ActionListener actionListener : listenerRegistry.getLocalActionListenersFor(modelContext)) {
            actionListener.processAction(modelContext, action);
        }
    }

}


