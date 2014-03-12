package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Thomas Spiegl
 */
public class WebSocketModelAddress implements ModelAddress {

    private final String clientId;
    private final String modelName;
    private final int hashCode;

    public WebSocketModelAddress(String clientId, String modelName) {
        this.clientId = clientId;
        this.modelName = modelName;
        this.hashCode = 31 * (31 * clientId.hashCode());
    }

    @Override
    public String getModelName() {
        return null;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "WebSocketModelAddress{" +
                "clientId='" + clientId + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
