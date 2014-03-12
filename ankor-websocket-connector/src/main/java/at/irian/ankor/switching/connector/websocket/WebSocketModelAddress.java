package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.switching.routing.ModelAddress;

/**
 * @author Thomas Spiegl
 */
public class WebSocketModelAddress implements ModelAddress {

    private final String remoteSystemId;
    private final String modelName;
    private final int hashCode;

    public WebSocketModelAddress(String remoteSystemId, String modelName) {
        this.remoteSystemId = remoteSystemId;
        this.modelName = modelName;
        this.hashCode = 31 * (31 * remoteSystemId.hashCode());
    }

    @Override
    public String getModelName() {
        return null;
    }

    public String getRemoteSystemId() {
        return remoteSystemId;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "WebSocketModelAddress{" +
                "remoteSystemId='" + remoteSystemId + '\'' +
                ", modelName='" + modelName + '\'' +
                '}';
    }
}
