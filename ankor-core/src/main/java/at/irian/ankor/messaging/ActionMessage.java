package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ActionMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionMessage.class);

    private Ref modelContext;
    private Action action;

    /**
     * for deserialization only
     */
    protected ActionMessage() {}

    protected ActionMessage(String messageId, Ref modelContext, Action action) {
        super(messageId);
        this.modelContext = modelContext;
        this.action = action;
    }

    public Ref getModelContext() {
        return modelContext;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
               "messageId='" + getMessageId() + '\'' +
               ", modelContext=" + modelContext +
               ", action=" + action +
               "}";
    }
}
