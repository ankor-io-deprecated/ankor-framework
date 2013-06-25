package at.irian.ankor.messaging;

import at.irian.ankor.action.Action;

/**
 * @author Manfred Geiler
 */
public class ActionMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionMessage.class);

    private String modelContextPath;
    private String actionPropertyPath;
    private Action action;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected ActionMessage() {}

    protected ActionMessage(String messageId, String modelContextPath, String actionPropertyPath, Action action) {
        super(messageId);
        this.modelContextPath = modelContextPath;
        this.actionPropertyPath = actionPropertyPath;
        this.action = action;
    }

    public String getModelContextPath() {
        return modelContextPath;
    }

    public String getActionPropertyPath() {
        return actionPropertyPath;
    }

    public Action getAction() {
        return action;
    }

    @Override
    public String toString() {
        return "ActionMessage{" +
               "modelContextPath='" + modelContextPath + '\'' +
               ", actionPropertyPath='" + actionPropertyPath + '\'' +
               ", action=" + action +
               '}';
    }
}
