package at.irian.ankor.connector.socket;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketMessage.class);

    private String senderId;
    private String property;
    private Action action;
    private Change change;
    private Map<String,Object> connectParams;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected SocketMessage() {}

    private SocketMessage(String senderId,
                          String property,
                          Action action,
                          Change change,
                          Map<String, Object> connectParams) {
        this.senderId = senderId;
        this.property = property;
        this.action = action;
        this.change = change;
        this.connectParams = connectParams;
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

    public Map<String, Object> getConnectParams() {
        return connectParams;
    }

    public static SocketMessage createActionMsg(String senderAddress, String property, Action action) {
        return new SocketMessage(senderAddress, property, action, null, null);
    }

    public static SocketMessage createChangeMsg(String senderAddress, String property, Change change) {
        return new SocketMessage(senderAddress, property, null, change, null);
    }

    public static SocketMessage createConnectMsg(String senderAddress,
                                                 String modelName,
                                                 Map<String, Object> connectParameters) {
        Map<String, Object> connParams = new HashMap<String, Object>();

        return new SocketMessage(senderAddress, modelName, null, null, connectParameters);
    }

}
