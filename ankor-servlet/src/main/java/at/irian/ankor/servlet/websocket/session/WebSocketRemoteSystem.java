package at.irian.ankor.servlet.websocket.session;

import at.irian.ankor.session.RemoteSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.util.concurrent.atomic.AtomicLong;

public class WebSocketRemoteSystem implements RemoteSystem {
    private static Logger LOG = LoggerFactory.getLogger(WebSocketRemoteSystem.class);

    private String id;
    private Session client;
    private AtomicLong lastSeen = new AtomicLong();

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

    public long getLastSeen() {
        return lastSeen.get();
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen.set(lastSeen);
    }
}
