package at.irian.ankor.core.application;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.listener.ActionListener;
import at.irian.ankor.core.listener.ListenerRegistry;
import at.irian.ankor.core.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class DefaultActionNotifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultActionNotifier.class);

    private final ListenerRegistry listenerRegistry;

    public DefaultActionNotifier(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public void broadcastAction(Ref contextRef, ModelAction action) {
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


