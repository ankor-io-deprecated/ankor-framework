package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.connection.ModelConnection;
import at.irian.ankor.connection.ModelConnectionManager;

import java.util.Collection;

/**
 * Global ActionEventListener that relays locally happened {@link ActionEvent ActionEvents} to all remote systems
 * connected to the underlying ModelSession.
 *
 * @author Manfred Geiler
 */
public class RemoteNotifyActionEventListener extends ActionEventListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteNotifyActionEventListener.class);

    private final MessageFactory messageFactory;
    private final ModelConnectionManager modelConnectionManager;
    private final Modifier preSendModifier;

    public RemoteNotifyActionEventListener(MessageFactory messageFactory,
                                           ModelConnectionManager modelConnectionManager,
                                           Modifier preSendModifier) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.modelConnectionManager = modelConnectionManager;
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
        ModelSession modelSession = actionProperty.context().modelSession();
        Collection<ModelConnection> modelConnections = modelConnectionManager.getAllFor(modelSession);
        for (ModelConnection modelConnection : modelConnections) {
            if (event.getSource() instanceof RemoteSource) {
                ModelConnection initiatingModelConnection = ((RemoteSource) event.getSource()).getModelConnection();
                if (modelConnection.equals(initiatingModelConnection)) {
                    // do not relay remote actions back to the remote system
                    continue;
                }
            }

            String actionPropertyPath = actionProperty.path();
            Message message = messageFactory.createActionMessage(actionProperty.context().modelSession(),
                                                                 actionPropertyPath,
                                                                 modifiedAction);
            LOG.debug("server sends {}", message);
            modelConnection.getMessageSender().sendMessage(message);
        }
    }
}
