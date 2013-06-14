package at.irian.ankor.impl.msgbus;

import at.irian.ankor.api.msgbus_deprecated.ClientToServerMsg;
import at.irian.ankor.api.msgbus_deprecated.MessageBus;
import at.irian.ankor.api.msgbus_deprecated.ServerToClientMsg;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MockMessageBus implements MessageBus {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultMessageBus.class);

    private ClientToServerMsg clientToServerMsg;
    private ServerToClientMsg serverToClientMsg;

    @Override
    public ClientToServerMsg getMessageFromClient() {
        return clientToServerMsg;
    }

    @Override
    public void sendMessageToClient(ServerToClientMsg msg) {
        this.serverToClientMsg = msg;
    }


    public ClientToServerMsg getClientToServerMsg() {
        return clientToServerMsg;
    }

    public void setClientToServerMsg(ClientToServerMsg clientToServerMsg) {
        this.clientToServerMsg = clientToServerMsg;
    }

    public ServerToClientMsg getServerToClientMsg() {
        return serverToClientMsg;
    }

    public void setServerToClientMsg(ServerToClientMsg serverToClientMsg) {
        this.serverToClientMsg = serverToClientMsg;
    }
}
