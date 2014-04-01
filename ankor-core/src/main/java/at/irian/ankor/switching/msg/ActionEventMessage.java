package at.irian.ankor.switching.msg;

import at.irian.ankor.action.Action;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ActionEventMessage implements EventMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionEventMessage.class);

    private String property;
    private Action action;
    private Map<String, Object> state;

    public ActionEventMessage(String property, Action action, Map<String, Object> state) {
        this.property = property;
        this.action = action;
        this.state = state;
    }

    public String getProperty() {
        return property;
    }

    public Action getAction() {
        return action;
    }

    public Map<String, Object> getState() {
        return state;
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

        ActionEventMessage that = (ActionEventMessage) o;

        if (action != null ? !action.equals(that.action) : that.action != null) {
            return false;
        }
        if (property != null ? !property.equals(that.property) : that.property != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = 31 * result + (action != null ? action.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ActionEventMessage{" +
               "property='" + property + '\'' +
               ", action=" + action +
               ", state=" + state +
               '}';
    }
}
