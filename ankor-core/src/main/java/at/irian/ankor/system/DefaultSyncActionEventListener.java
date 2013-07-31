package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.Ref;

/**
 * Global ActionEventListener that relays all locally happened {@link ActionEvent ActionEvents} to the remote system.
 *
 * @author Manfred Geiler
 */
public class DefaultSyncActionEventListener extends ActionEventListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSyncActionEventListener.class);

    private final MessageFactory messageFactory;
    private final MessageSender messageSender;

    public DefaultSyncActionEventListener(MessageFactory messageFactory, MessageSender messageSender) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.messageSender = messageSender;
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ActionEvent event) {
        Action action = event.getAction();
        if (action instanceof RemoteEvent.Action) {
            // do not relay remote actions back to the remote system
        } else {
            LOG.debug("processing local action event {}", event);
            Ref actionProperty = event.getActionProperty();
            String actionPropertyPath = actionProperty.path();
            Message message = messageFactory.createActionMessage(actionProperty.context().modelContext(),
                                                                 actionPropertyPath,
                                                                 action);
            messageSender.sendMessage(message);
        }
    }
}
