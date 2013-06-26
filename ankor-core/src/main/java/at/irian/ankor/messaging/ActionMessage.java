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

        if (!actionPropertyPath.equals(that.actionPropertyPath)) {
            return false;
        }
        if (!action.equals(that.action)) {
            return false;
        }
        if (modelContextPath != null
            ? !modelContextPath.equals(that.modelContextPath)
            : that.modelContextPath != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelContextPath != null ? modelContextPath.hashCode() : 0;
        result = 31 * result + actionPropertyPath.hashCode();
        result = 31 * result + action.hashCode();
        return result;
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
