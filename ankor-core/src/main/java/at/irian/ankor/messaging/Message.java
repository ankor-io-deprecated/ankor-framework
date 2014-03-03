package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
@Deprecated
public abstract class Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Message.class);

    private String senderId;
    private String modelId;
    private String messageId;

    /**
     * for deserialization only
     */
    protected Message() {}

    protected Message(String senderId, String modelId, String messageId) {
        this.senderId = senderId;
        this.modelId = modelId;
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getModelId() {
        return modelId;
    }

    public String getMessageId() {
        return messageId;
    }

    @Override
    public String toString() {
        return "Message{" +
               "messageId='" + messageId + '\'' +
               "}";
    }


    public abstract boolean isAppropriateListener(MessageListener listener);

    public abstract void processBy(MessageListener listener);
}
