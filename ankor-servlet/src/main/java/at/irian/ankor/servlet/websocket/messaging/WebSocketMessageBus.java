package at.irian.ankor.servlet.websocket.messaging;

import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.RemoteSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketMessageBus extends MessageBus<String> {
    private static int EXPECTED_CONCURRENT_REQUESTS = 2;

    private static Logger LOG = LoggerFactory.getLogger(WebSocketMessageBus.class);
    private Map<String, WebSocketRemoteSystem> remoteSystems =
            new ConcurrentHashMap<String, WebSocketRemoteSystem>(1, 0.9f, EXPECTED_CONCURRENT_REQUESTS);

    public WebSocketMessageBus(ViewModelJsonMessageMapper mapper) {
        super(mapper, mapper);
    }

    @Override
    protected void sendSerializedMessage(String remoteSystemId, String msg) {
        WebSocketRemoteSystem remoteSystem = remoteSystems.get(remoteSystemId);
        if (remoteSystem != null) {
            Session client = remoteSystem.getClient();

            LOG.debug("Send serialized message {} to client {}", msg, client.getId());
            try {
                client.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                LOG.error("Error while sending message.");
            }
        }
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return remoteSystems.values();
    }

    public void addRemoteSystem(WebSocketRemoteSystem remoteSystem) {
        remoteSystems.put(remoteSystem.getId(), remoteSystem);
    }

    public RemoteSystem removeRemoteSystem(String clientId) {
        return remoteSystems.remove(clientId);
    }
}
