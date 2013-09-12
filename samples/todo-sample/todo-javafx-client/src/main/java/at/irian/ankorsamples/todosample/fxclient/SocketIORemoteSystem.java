package at.irian.ankorsamples.todosample.fxclient;

import at.irian.ankor.session.RemoteSystem;
import io.socket.SocketIO;

public class SocketIORemoteSystem implements RemoteSystem {

    private String id;
    private SocketIO socket;

    public SocketIORemoteSystem(String id, SocketIO socket) {
        this.id = id;
        this.socket = socket;
    }

    @Override
    public String getId() {
        return id;
    }

    public SocketIO getSocket() {
        return socket;
    }
}
