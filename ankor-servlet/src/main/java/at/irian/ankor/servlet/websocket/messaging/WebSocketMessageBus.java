package at.irian.ankor.servlet.websocket.messaging;

import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.servlet.websocket.session.WebSocketRemoteSystem;
import at.irian.ankor.session.RemoteSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;

public class WebSocketMessageBus extends MessageBus<String> {
    private static Logger LOG = LoggerFactory.getLogger(WebSocketMessageBus.class);

    private HashMap<String, WebSocketRemoteSystem> remoteSystems = new HashMap<String, WebSocketRemoteSystem>();

    public WebSocketMessageBus(ViewModelJsonMessageMapper mapper) {
        super(mapper, mapper);
    }

    public void addRemoteSystem(WebSocketRemoteSystem remoteSystem) {
        remoteSystems.put(remoteSystem.getId(), remoteSystem);
    }

    @Override
    protected void sendSerializedMessage(String remoteSystemId, String msg) {
        WebSocketRemoteSystem remoteSystem = remoteSystems.get(remoteSystemId);
        if (remoteSystem != null) {
            remoteSystem.sendMessage(msg);
        }
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return remoteSystems.values();
    }

    public RemoteSystem removeRemoteSystem(String clientId) {
        return remoteSystems.remove(clientId);
    }
}
