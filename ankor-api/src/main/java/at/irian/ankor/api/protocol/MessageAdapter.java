package at.irian.ankor.api.protocol;

/**
 * @author MGeiler (Manfred Geiler)
 */
public interface MessageAdapter {
    Object getMessageFromClient();
    void sendMessageToClient(Object message);
}
