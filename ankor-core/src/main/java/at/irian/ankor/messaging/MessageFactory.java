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

    public Message createActionMessage(String sessionId, String actionPropertyPath, Action action) {
        return new ActionMessage(sessionId, messageUUIDGenerator.createId(), actionPropertyPath, action);
    }

    public Message createChangeMessage(String sessionId, String changedPropertyPath, Object newValue) {
        return new ChangeMessage(sessionId, messageUUIDGenerator.createId(), changedPropertyPath, newValue);
    }

}
