package at.irian.ankor.http;

import at.irian.ankor.connection.SimpleRemoteSystem;

/**
 * @author Manfred Geiler
 */
@Deprecated
public class ServerHost extends SimpleRemoteSystem {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerHost.class);

    private final String url;

    public ServerHost(String id, String url) {
        super(id);
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ServerHost{" +
               "url='" + url + '\'' +
               "} " + super.toString();
    }
}
