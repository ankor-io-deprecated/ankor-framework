package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.source.RemoteSource;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.modify.Modifier;
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
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyActionEventListener.class);

    private final MessageFactory messageFactory;
    private final SessionManager sessionManager;
    private final Modifier preSendModifier;

    public RemoteNotifyActionEventListener(MessageFactory messageFactory,
                                           SessionManager sessionManager,
                                           Modifier preSendModifier) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.sessionManager = sessionManager;
        this.preSendModifier = preSendModifier;
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
        Action modifiedAction = preSendModifier.modifyBeforeSend(action, actionProperty);
        ModelContext modelContext = actionProperty.context().modelContext();
        Collection<Session> sessions = sessionManager.getAllFor(modelContext);
        for (Session session : sessions) {
            if (event.getSource() instanceof RemoteSource) {
                Session initiatingSession = ((RemoteSource) event.getSource()).getSession();
                if (session.equals(initiatingSession)) {
                    // do not relay remote actions back to the remote system
                    continue;
                }
            }

            String actionPropertyPath = actionProperty.path();
            Message message = messageFactory.createActionMessage(actionProperty.context().modelContext(),
                                                                 actionPropertyPath,
                                                                 modifiedAction);
            LOG.debug("server sends {}", message);
            session.getMessageSender().sendMessage(message);
        }
    }
}
