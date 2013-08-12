package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.action.RemoteAction;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.session.Session;
import at.irian.ankor.session.SessionManager;

import java.util.Collection;

/**
 * Global ActionEventListener that relays locally happened {@link ActionEvent ActionEvents} to all remote systems
 * connected to the underlying ModelContext.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyActionEventListener extends ActionEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyActionEventListener.class);

    private final MessageFactory messageFactory;
    private final SessionManager sessionManager;

    public RemoteNotifyActionEventListener(MessageFactory messageFactory,
                                           SessionManager sessionManager) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.sessionManager = sessionManager;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ActionEvent event) {
        Action action = event.getAction();
        Ref actionProperty = event.getActionProperty();
        ModelContext modelContext = actionProperty.context().modelContext();
        Collection<Session> sessions = sessionManager.getAllFor(modelContext);
        for (Session session : sessions) {
            if (action instanceof RemoteAction) {
                Session initiatingSession = ((RemoteAction) action).getSession();
                if (session.equals(initiatingSession)) {
                    // do not relay remote actions back to the remote system
                    continue;
                }
            }

            String actionPropertyPath = actionProperty.path();
            Message message = messageFactory.createActionMessage(actionProperty.context().modelContext(),
                                                                 actionPropertyPath,
                                                                 action);
            session.getMessageSender().sendMessage(message);
        }
    }
}
