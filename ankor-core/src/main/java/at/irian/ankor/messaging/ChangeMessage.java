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

    protected ChangeMessage(String sessionId, String messageId,
                            String changedProperty, Change change) {
        super(sessionId, messageId);
        this.property = changedProperty;
        this.change = change;
    }

    public String getProperty() {
        return property;
    }

    public Change getChange() {
        return change;
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ChangeMessage that = (ChangeMessage) o;

        if (!change.equals(that.change)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return change.hashCode();
    }

    @Override
    public String toString() {
        return "ChangeMessage{" +
               "messageId='" + getMessageId() + '\'' +
               ", change=" + change +
               "}";
    }
}
