package at.irian.ankor.gateway.party;

import java.net.URI;

/**
 * @author Manfred Geiler
 */
public class SocketParty implements Party {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketParty.class);

    private final String host;
    private final int port;
    private final String modelName;

    @SuppressWarnings("UnusedDeclaration")
    public SocketParty(String host, int port, String modelName) {
        this.host = host;
        this.port = port;
        this.modelName = modelName;
    }

    public SocketParty(URI address, String modelName) {
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

        SocketParty that = (SocketParty) o;

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
        return "SocketParty{" +
               "host='" + host + '\'' +
               ", port=" + port +
               ", modelName='" + modelName + '\'' +
               '}';
    }


}
