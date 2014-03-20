package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.switching.connector.ConnectionHandler;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.routing.ModelAddress;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketConnectionHandler implements ConnectionHandler<SocketModelAddress> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnectionHandler.class);

    private final URI localAddress;
    private final MessageSerializer<String> messageSerializer;

    public SocketConnectionHandler(URI localAddress,
                                   MessageSerializer<String> messageSerializer) {
        this.localAddress = localAddress;
        this.messageSerializer = messageSerializer;
    }

    @Override
    public void openConnection(ModelAddress sender,
                               SocketModelAddress receiver,
                               Map<String, Object> connectParameters,
                               HandlerScopeContext context) {
        SocketMessage connectMsg = SocketMessage.createConnectMsg(localAddress.toString(),
                                                                     sender.getModelName(),
                                                                     connectParameters);
        send(sender, receiver, connectMsg);
    }

    @Override
    public void closeConnection(ModelAddress sender,
                                SocketModelAddress receiver,
                                boolean lastRoute,
                                HandlerScopeContext context) {
        SocketMessage closeMsg = SocketMessage.createCloseMsg(localAddress.toString(),
                                                              sender.getModelName());
        send(sender, receiver, closeMsg);
    }


    private void send(ModelAddress sender, SocketModelAddress receiver, SocketMessage socketMessage) {
        try {
            new SocketSender(messageSerializer).send(receiver, socketMessage);
        } catch (IOException e) {
            LOG.error("Error sending {} from {} to {}", socketMessage, sender, receiver);
        }
    }


}
