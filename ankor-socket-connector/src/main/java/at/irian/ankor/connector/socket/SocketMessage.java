package at.irian.ankor.connector.socket;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;

/**
 * @author Manfred Geiler
 */
public class SocketMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketMessage.class);

    private String senderId;
    private String property;
    private Action action;
    private Change change;

    /**
     * for deserialization only
     */
    protected SocketMessage() {}

    public SocketMessage(String senderId, String property, Action action, Change change) {
        this.senderId = senderId;
        this.property = property;
        this.action = action;
        this.change = change;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getProperty() {
        return property;
    }

    public Action getAction() {
        return action;
    }

    public Change getChange() {
        return change;
    }
}
