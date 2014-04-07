package at.irian.ankor.switching.msg;

import at.irian.ankor.action.Action;

import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class ActionEventMessage implements EventMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionEventMessage.class);

    private String property;
    private Action action;
    private Map<String, Object> stateValues;
    private Set<String> stateHolderProperties;

    public ActionEventMessage(String property,
                              Action action,
                              Map<String, Object> stateValues,
                              Set<String> stateHolderProperties) {
        this.property = property;
        this.action = action;
        this.stateValues = stateValues;
        this.stateHolderProperties = stateHolderProperties;
    }

    public String getProperty() {
        return property;
    }

    public Action getAction() {
        return action;
    }

    public Map<String, Object> getStateValues() {
        return stateValues;
    }

    public Set<String> getStateHolderProperties() {
        return stateHolderProperties;
    }

    @Override
    public String toString() {
        return "ActionEventMessage{" +
               "property='" + property + '\'' +
               ", action=" + action +
               ", stateValues=" + stateValues +
               ", stateHolderProperties=" + stateHolderProperties +
               '}';
    }
}
