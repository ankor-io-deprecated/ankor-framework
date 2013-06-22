package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;

/**
 * @author Manfred Geiler
 */
public class ActionMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionMessage.class);

    private Action action;

    protected ActionMessage() {
    }

    public ActionMessage(String messageId, Action action) {
        super(messageId);
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
               "action=" + action +
               "} " + super.toString();
    }
}
