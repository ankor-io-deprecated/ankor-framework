package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.Ref;

/**
 * Global ChangeEventListener that relays all locally happened {@link ChangeEvent ChangeEvents} to the remote system.
 *
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

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public void process(ChangeEvent event) {
        Change change = event.getChange();
        if (change instanceof RemoteEvent.Change) {
            // do not relay remote changes back to remote partner ...
        } else {
            LOG.debug("processing local change event {}", event);
            Ref changedProperty = event.getChangedProperty();
            String changedPropertyPath = changedProperty.path();
            Message message = messageFactory.createChangeMessage(changedProperty.context().modelContext(),
                                                                 changedPropertyPath,
                                                                 change);
            messageSender.sendMessage(message);
        }
    }
}
