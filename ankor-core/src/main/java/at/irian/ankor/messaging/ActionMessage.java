package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;

/**
 * @author Manfred Geiler
 */
public class ActionMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionMessage.class);

    private String actionProperty;
    private Action action;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected ActionMessage() {}

    protected ActionMessage(String sessionId, String messageId,
                            String actionProperty, Action action) {
        super(sessionId, messageId);
        this.actionProperty = actionProperty;
        this.action = action;
    }

    public String getActionProperty() {
        return actionProperty;
    }

    public Action getAction() {
        return action;
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

        ActionMessage that = (ActionMessage) o;

        if (!actionProperty.equals(that.actionProperty)) {
            return false;
        }
        if (!action.equals(that.action)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = actionProperty.hashCode();
        result = 31 * result + action.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
               "messageId='" + getMessageId() + '\'' +
               ", actionProperty='" + actionProperty + '\'' +
               ", action=" + action +
               '}';
    }
}
