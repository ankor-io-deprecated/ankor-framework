package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class DefaultSyncChangeEventListener extends ChangeEventListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSyncChangeEventListener.class);

    private final MessageFactory messageFactory;
    private final MessageSender messageSender;

    public DefaultSyncChangeEventListener(MessageFactory messageFactory, MessageSender messageSender) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.messageSender = messageSender;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ChangeEvent event) {
        Change change = event.getChange();
        if (change instanceof RemoteChange) {
            // do not relay remote changes back to remote partner ...
        } else {
            LOG.info("processing local change event {}", event);
            Ref changedProperty = event.getChangedProperty();
            String changedPropertyPath = changedProperty.path();
            Message message = messageFactory.createChangeMessage(changedProperty.context().session().getId(),
                                                                 changedPropertyPath, change);
            messageSender.sendMessage(message);
        }
    }
}
