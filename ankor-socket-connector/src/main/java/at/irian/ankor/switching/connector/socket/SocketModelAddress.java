package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.switching.routing.ModelAddress;

import java.io.Serializable;
import java.net.URI;

/**
 * @author Manfred Geiler
 */
public class SocketModelAddress implements ModelAddress, Serializable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketModelAddress.class);

    private final String host;
    private final int port;
    private final String modelName;
    private final int hashCode;
    private String consistentHashKey;

    @SuppressWarnings("UnusedDeclaration")
    public SocketModelAddress(String host, int port, String modelName) {
        this.host = host;
        this.port = port;
        this.modelName = modelName;
        this.hashCode = 31 * (31 * host.hashCode() + port) + modelName.hashCode();
        this.consistentHashKey = host + port + modelName;
    }

    public SocketModelAddress(URI address, String modelName) {
        this(address.getHost(), address.getPort(), modelName);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SocketModelAddress that = (SocketModelAddress) o;

        return port == that.port && host.equals(that.host) && modelName.equals(that.modelName);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String consistentHashKey() {
        return consistentHashKey;
    }

    @Override
    public String toString() {
        return "SocketModelAddress{" +
               "host='" + host + '\'' +
               ", port=" + port +
               ", modelName='" + modelName + '\'' +
               '}';
    }


}
