package at.irian.ankor.application;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionListener;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class DefaultActionNotifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultActionNotifier.class);

    private final ListenerRegistry listenerRegistry;

    public DefaultActionNotifier(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void broadcastAction(Ref contextRef, Action action) {
        if (contextRef == null) {
            throw new NullPointerException("contextRef");
        }
        if (action == null) {
            throw new NullPointerException("action");
        }
        for (ActionListener actionListener : listenerRegistry.getLocalActionListenersFor(contextRef)) {
            actionListener.processAction(contextRef, action);
        }
    }

}


