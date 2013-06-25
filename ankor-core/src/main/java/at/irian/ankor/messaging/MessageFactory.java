package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;

/**
 * @author Manfred Geiler
 */
public class MessageFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageFactory.class);

    private final MessageUUIDGenerator messageUUIDGenerator;

    public MessageFactory() {
        this.messageUUIDGenerator = new MessageUUIDGenerator();
    }

    public Message createActionMessage(String modelContextPath, String actionPropertyPath, Action action) {
        return new ActionMessage(messageUUIDGenerator.createId(), modelContextPath, actionPropertyPath, action);
    }

    public <T> Message createChangeMessage(String modelContextPath, String changedPropertyPath, T newValue) {
        return new ChangeMessage(messageUUIDGenerator.createId(), modelContextPath, changedPropertyPath, newValue);
    }

}
