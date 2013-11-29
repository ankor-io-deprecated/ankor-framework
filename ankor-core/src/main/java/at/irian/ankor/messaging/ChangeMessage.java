package at.irian.ankor.messaging;

import at.irian.ankor.change.Change;

/**
 * @author Manfred Geiler
 */
public class ChangeMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeMessage.class);

    private String property;
    private Change change;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected ChangeMessage() {}

    protected ChangeMessage(String senderId, String modelId, String messageId,
                            String changedProperty, Change change) {
        super(senderId, modelId, messageId);
        this.property = changedProperty;
        this.change = change;
    }

    public String getProperty() {
        return property;
    }

    public Change getChange() {
        return change;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChangeMessage that = (ChangeMessage) o;

        return change.equals(that.change);
    }

    @Override
    public int hashCode() {
        return change.hashCode();
    }

    @Override
    public String toString() {
        return "ChangeMessage{" +
               "messageId='" + getMessageId() + '\'' +
               ", property='" + property + '\'' +
               ", change=" + change +
               "} " + super.toString();
    }


    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onChangeMessage(this);
    }

    public interface Listener extends MessageListener {
        void onChangeMessage(ChangeMessage msg);
    }

}
