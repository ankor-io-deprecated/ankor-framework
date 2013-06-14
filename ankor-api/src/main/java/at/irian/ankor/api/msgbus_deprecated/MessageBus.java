package at.irian.ankor.api.msgbus_deprecated;

/**
 */
public interface MessageBus {

    ClientToServerMsg getMessageFromClient();

    void sendMessageToClient(ServerToClientMsg msg);

}
