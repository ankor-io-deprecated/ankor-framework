package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class MessageFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageFactory.class);

    private final MessageUUIDGenerator messageUUIDGenerator;

    public MessageFactory() {
        this.messageUUIDGenerator = new MessageUUIDGenerator();
    }

    public Message createActionMessage(Ref modelContext, Action action) {
        return new ActionMessage(messageUUIDGenerator.createId(), modelContext, action);
    }

    public <T> Message createChangeMessage(Ref modelContext, Ref changedProperty, T newValue) {
        return new ChangeMessage(messageUUIDGenerator.createId(), modelContext, changedProperty, newValue);
    }

}
