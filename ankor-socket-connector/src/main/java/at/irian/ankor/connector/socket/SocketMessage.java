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

    private SocketMessage(String senderId, String property, Action action, Change change) {
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



    public static SocketMessage createActionMsg(String senderAddress, String property, Action action) {
        return new SocketMessage(senderAddress, property, action, null);
    }

    public static SocketMessage createChangeMsg(String senderAddress, String property, Change change) {
        return new SocketMessage(senderAddress, property, null, change);
    }

    public static SocketMessage createConnectMsg(String senderAddress, String modelName) {
        return new SocketMessage(senderAddress, modelName, null, null);
    }

}
