package at.irian.ankor.connector.socket;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class SocketParty implements Party {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketParty.class);

    private final String id;
    private final String host;
    private final int port;

    public SocketParty(String id, String host, int port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "SocketParty{" +
               "id='" + id + '\'' +
               ", host='" + host + '\'' +
               ", port=" + port +
               '}';
    }
}
