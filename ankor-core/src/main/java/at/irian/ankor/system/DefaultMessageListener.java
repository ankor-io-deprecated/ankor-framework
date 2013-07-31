package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.messaging.ActionMessage;
import at.irian.ankor.messaging.ChangeMessage;
import at.irian.ankor.messaging.MessageListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.Session;
import at.irian.ankor.session.SessionManager;

import static at.irian.ankor.system.RemoteEvent.createActionEvent;
import static at.irian.ankor.system.RemoteEvent.createChangeEvent;

/**
 * Main system message listener that propagates remote events (actions and changes)
 * to the local {@linkplain AnkorSystem Ankor system's}
 * {@link at.irian.ankor.event.dispatch.EventDispatcher EventDispatcher}.
 *
* @author Manfred Geiler
*/
public class DefaultMessageListener implements MessageListener {

    private final SessionManager sessionManager;

    DefaultMessageListener(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void onActionMessage(ActionMessage message) {
        String sessionId = message.getSessionId();
        Session session = sessionManager.getOrCreateSession(sessionId);

        RefContext refContext = session.getRefContext();
        Ref actionProperty = refContext.refFactory().ref(message.getProperty());

        Action action = message.getAction();
        RemoteEvent event = createActionEvent(actionProperty, action.getName(), action.getParams());
        session.getEventDispatcher().dispatch(event);
    }

    @Override
    public void onChangeMessage(ChangeMessage message) {
        String sessionId = message.getSessionId();
        Session session = sessionManager.getOrCreateSession(sessionId);

        RefContext refContext = session.getRefContext();
        Ref changedProperty = refContext.refFactory().ref(message.getProperty());

        RemoteEvent event = createChangeEvent(changedProperty, message.getChange().getNewValue());
        session.getEventDispatcher().dispatch(event);
    }
}
