package at.irian.ankor.impl.msgbus;

import at.irian.ankor.api.msgbus_deprecated.ClientToServerMsg;
import at.irian.ankor.api.msgbus_deprecated.MessageBus;
import at.irian.ankor.api.msgbus_deprecated.ServerToClientMsg;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultMessageBus implements MessageBus {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultMessageBus.class);


    @Override
    public ClientToServerMsg getMessageFromClient() {
        throw new UnsupportedOperationException("not yet");
    }

    @Override
    public void sendMessageToClient(ServerToClientMsg msg) {
        throw new UnsupportedOperationException("not yet");
    }

}
