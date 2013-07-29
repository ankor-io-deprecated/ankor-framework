package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.Ref;

/**
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ActionEvent event) {
        Action action = event.getAction();
        if (action instanceof RemoteAction) {
            // do not relay remote action back to remote partner ...
        } else {
            LOG.info("processing local action event {}", event);
            Ref actionProperty = event.getActionProperty();
            String actionPropertyPath = actionProperty.path();
            Message message = messageFactory.createActionMessage(actionProperty.context().session().getId(),
                                                                 actionPropertyPath,
                                                                 action);
            messageSender.sendMessage(message);
        }
    }
}
