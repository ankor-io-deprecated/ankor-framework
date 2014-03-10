package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.switching.connector.ConnectionHandler;
import at.irian.ankor.switching.party.Party;
import at.irian.ankor.switching.party.SocketParty;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class SocketConnectionHandler implements ConnectionHandler<SocketParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnectionHandler.class);

    private final URI localAddress;
    private final MessageSerializer<String> messageSerializer;

    public SocketConnectionHandler(URI localAddress,
                                   MessageSerializer<String> messageSerializer) {
        this.localAddress = localAddress;
        this.messageSerializer = messageSerializer;
    }

    @Override
    public void openConnection(Party sender, SocketParty receiver, Map<String, Object> connectParameters) {
        SocketMessage connectMsg = SocketMessage.createConnectMsg(localAddress.toString(),
                                                                     sender.getModelName(),
                                                                     connectParameters);
        send(sender, receiver, connectMsg);
    }

    @Override
    public void closeConnection(Party sender, SocketParty receiver, boolean lastRoute) {
        SocketMessage closeMsg = SocketMessage.createCloseMsg(localAddress.toString(),
                                                              sender.getModelName());
        send(sender, receiver, closeMsg);
    }


    private void send(Party sender, SocketParty receiver, SocketMessage socketMessage) {
        try {
            new SocketSender(messageSerializer).send(receiver, socketMessage);
        } catch (IOException e) {
            LOG.error("Error sending {} from {} to {}", socketMessage, sender, receiver);
        }
    }


}
