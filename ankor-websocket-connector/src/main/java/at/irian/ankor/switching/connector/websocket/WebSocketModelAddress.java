package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.switching.routing.ModelAddress;

import java.io.Serializable;

/**
 * @author Thomas Spiegl
 */
public class WebSocketModelAddress implements ModelAddress, Serializable {

    private final String clientId;
    private final String modelName;
    private final int hashCode;

    public WebSocketModelAddress(String clientId, String modelName) {
        this.clientId = clientId;
        this.modelName = modelName;
        this.hashCode = 31 * clientId.hashCode() + modelName.hashCode();
    }

    @Override
    public String getModelName() {
        return null;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WebSocketModelAddress that = (WebSocketModelAddress) o;

        return clientId.equals(that.clientId) && modelName.equals(that.modelName);
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
