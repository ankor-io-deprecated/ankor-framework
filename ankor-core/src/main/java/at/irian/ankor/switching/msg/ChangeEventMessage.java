package at.irian.ankor.switching.msg;

import at.irian.ankor.change.Change;

import java.util.Map;
import java.util.Set;

/**
 * @author Manfred Geiler
 */
public class ChangeEventMessage implements EventMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeEventMessage.class);

    private String property;
    private Change change;
    private Map<String, Object> stateValues;
    private Set<String> stateHolderProperties;

    public ChangeEventMessage(String property,
                              Change change,
                              Map<String, Object> stateValues,
                              Set<String> stateHolderProperties) {
        this.property = property;
        this.change = change;
        this.stateValues = stateValues;
        this.stateHolderProperties = stateHolderProperties;
    }

    public String getProperty() {
        return property;
    }

    public Change getChange() {
        return change;
    }

    public Map<String, Object> getStateValues() {
        return stateValues;
    }

    public Set<String> getStateHolderProperties() {
        return stateHolderProperties;
    }

    @Override
    public String toString() {
        return "ChangeEventMessage{" +
               "property='" + property + '\'' +
               ", change=" + change +
               ", stateValues=" + stateValues +
               ", stateHolderProperties=" + stateHolderProperties +
               '}';
    }
}
