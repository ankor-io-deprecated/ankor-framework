package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public abstract class Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Message.class);
    
    protected String messageId;

    protected Message() {
    }

    protected Message(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "Message{" +
               "messageId='" + messageId + '\'' +
               "} " + super.toString();
    }
}
