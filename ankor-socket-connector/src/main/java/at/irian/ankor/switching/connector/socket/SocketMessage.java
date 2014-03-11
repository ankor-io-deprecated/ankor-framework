package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketMessage.class);

    @JsonInclude(JsonInclude.Include.ALWAYS)
    private String senderId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String property;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Action action;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Change change;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String,Object> connectParams;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean close;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    protected SocketMessage() {}

    private SocketMessage(String senderId,
                          String property,
                          Action action,
                          Change change,
                          Map<String, Object> connectParams,
                          boolean close) {
        this.senderId = senderId;
        this.property = property;
        this.action = action;
        this.change = change;
        this.connectParams = connectParams;
        this.close = close;
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

    public boolean isClose() {
        return close;
    }

    public static SocketMessage createActionMsg(String senderAddress, String property, Action action) {
        return new SocketMessage(senderAddress, property, action, null, null, false);
    }

    public static SocketMessage createChangeMsg(String senderAddress, String property, Change change) {
        return new SocketMessage(senderAddress, property, null, change, null, false);
    }

    public static SocketMessage createConnectMsg(String senderAddress,
                                                 String modelName,
                                                 Map<String, Object> connectParameters) {
        return new SocketMessage(senderAddress, modelName, null, null, connectParameters, false);
    }

    public static SocketMessage createCloseMsg(String senderAddress, String modelName) {
        return new SocketMessage(senderAddress, modelName, null, null, null, true);
    }

    @Override
    public String toString() {
        return "SocketMessage{" +
               "senderId='" + senderId + '\'' +
               ", property='" + property + '\'' +
               ", action=" + action +
               ", change=" + change +
               ", connectParams=" + connectParams +
               ", close=" + close +
               '}';
    }
}
