package at.irian.ankor.system;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.messaging.Message;
import at.irian.ankor.messaging.MessageFactory;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public class DefaultSyncChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultSyncChangeEventListener.class);

    private final MessageFactory messageFactory;
    private final Session session;

    public DefaultSyncChangeEventListener(MessageFactory messageFactory,
                                          Session session) {
        super(null); //global listener
        this.messageFactory = messageFactory;
        this.session = session;
    }

    @Override
    public void process(ChangeEvent event) {
        Ref changedProperty = event.getChangedProperty();
        Object newValue = changedProperty.getValue();
        RefContext refContext = changedProperty.context();
        String changedPropertyPath = changedProperty.path();
        Message message = messageFactory.createChangeMessage(session.getId(), changedPropertyPath, newValue);
        session.getMessageSender().sendMessage(message);

        // in addition to sending change events to remote, cleanup orphaned listeners
        if (newValue == null) {
            refContext.eventListeners().cleanup();
        }
    }
}
