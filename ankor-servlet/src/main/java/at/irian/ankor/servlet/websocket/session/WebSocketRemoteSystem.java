package at.irian.ankor.servlet.websocket.session;

import at.irian.ankor.session.RemoteSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;

public class WebSocketRemoteSystem implements RemoteSystem {
    private static Logger LOG = LoggerFactory.getLogger(WebSocketRemoteSystem.class);

    private String id;
    private Session client;

    public WebSocketRemoteSystem(String id, Session client) {
        this.id = id;
        this.client = client;
    }

    @Override
    public String getId() {
        return id;
    }

    public Session getClient() {
        return client;
    }
}
