package at.irian.ankorsamples.todosample.servlet;

import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.session.RemoteSystem;

import java.util.Collection;
import java.util.HashMap;

public class AtmosphereMessageBus extends MessageBus<String> {

    private HashMap<String, AtmosphereRemoteSystem> remoteSystems = new HashMap<>();

    public AtmosphereMessageBus(ViewModelJsonMessageMapper messageMapper) {
        super(messageMapper, messageMapper);
    }

    @Override
    protected void sendSerializedMessage(String remoteSystemId, String msg) {
        AtmosphereRemoteSystem remoteSystem = remoteSystems.get(remoteSystemId);
        if (remoteSystem != null) {
            // remoteSystem.getResource().getResponse().write(msg + "\n");
            // remoteSystem.getResource().write(msg + "\n");
            remoteSystem.getResource().getResponse().write(msg + "\n", true);
        }
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return remoteSystems.values();
    }

    public synchronized void addRemoteSystem(AtmosphereRemoteSystem remoteSystem) {
        remoteSystems.put(remoteSystem.getId(), remoteSystem);
        remoteSystem.getResource().resumeOnBroadcast(false).suspend();
    }

    public void removeRemoteSystem(String uuid) {
        AtmosphereRemoteSystem remoteSystem = remoteSystems.remove(uuid);
        remoteSystem.getResource().resume();
    }
}
