package at.irian.ankorsamples.todosample.server.socketIO;

import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.session.RemoteSystem;

import java.util.Collection;
import java.util.HashMap;

public class SocketIOMessageBus extends MessageBus<String> {

    private HashMap<String, SocketIORemoteSystem> remoteSystems = new HashMap<>();

    public SocketIOMessageBus(ViewModelJsonMessageMapper messageMapper) {
        super(messageMapper, messageMapper);
    }

    @Override
    protected void sendSerializedMessage(String remoteSystemId, String msg) {
        final SocketIORemoteSystem remoteSystem = remoteSystems.get(remoteSystemId);

        if (remoteSystem != null) {
            remoteSystem.sendMessage(msg);
        }
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return remoteSystems.values();
    }

    public synchronized void addRemoteSystem(SocketIORemoteSystem remoteSystem) {
        remoteSystems.put(remoteSystem.getId(), remoteSystem);
    }

    public void removeRemoteSystem(String uuid) {
        remoteSystems.remove(uuid);
    }
}
