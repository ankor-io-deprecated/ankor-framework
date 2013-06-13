package at.irian.ankor.api.msgbus;

/**
 */
public interface MessageBus {

    ClientToServerMsg getMessageFromClient();

    void sendMessageToClient(ServerToClientMsg msg);

}
