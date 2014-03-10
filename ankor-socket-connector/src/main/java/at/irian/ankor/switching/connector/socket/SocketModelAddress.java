package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.switching.routing.ModelAddress;

import java.net.URI;

/**
 * @author Manfred Geiler
 */
public class SocketModelAddress implements ModelAddress {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketModelAddress.class);

    private final String host;
    private final int port;
    private final String modelName;

    @SuppressWarnings("UnusedDeclaration")
    public SocketModelAddress(String host, int port, String modelName) {
        this.host = host;
        this.port = port;
        this.modelName = modelName;
    }

    public SocketModelAddress(URI address, String modelName) {
        this.host = address.getHost();
        this.port = address.getPort();
        this.modelName = modelName;
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
        int result = host.hashCode();
        result = 31 * result + port;
        result = 31 * result + modelName.hashCode();
        return result;
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
