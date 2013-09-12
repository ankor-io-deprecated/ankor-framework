package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.messaging.MessageBus;
import at.irian.ankor.messaging.json.viewmodel.ViewModelJsonMessageMapper;
import at.irian.ankor.session.RemoteSystem;
import io.socket.SocketIO;
import javafx.application.Platform;

import java.util.Collection;
import java.util.HashMap;

public class SocketIOMessageBus extends MessageBus<String> {

    private HashMap<String, SocketIORemoteSystem> remoteSystems = new HashMap<>();

    public SocketIOMessageBus(ViewModelJsonMessageMapper mapper) {
        super(mapper, mapper);
    }

    public void addRemoteSystem(SocketIORemoteSystem remoteSystem) {
        remoteSystems.put(remoteSystem.getId(), remoteSystem);
    }

    @Override
    protected void sendSerializedMessage(String remoteSystemId, String msg) {
        SocketIORemoteSystem remoteSystem = remoteSystems.get(remoteSystemId);
        if (remoteSystem != null) {
            SocketIO socket = remoteSystem.getSocket();
            if (socket.isConnected()) {
                socket.send(msg);
            }
        }
    }

    @Override
    public Collection<? extends RemoteSystem> getKnownRemoteSystems() {
        return remoteSystems.values();
    }

    @Override
    public void receiveSerializedMessage(final String msg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                receiveMessage(messageDeserializer.deserialize(msg));
            }
        });
    }
}

