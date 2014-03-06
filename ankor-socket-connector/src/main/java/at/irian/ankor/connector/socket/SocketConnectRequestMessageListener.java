package at.irian.ankor.connector.socket;

import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.msg.ConnectRequestMessage;
import at.irian.ankor.msg.RoutingTable;
import at.irian.ankor.msg.party.Party;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
* @author Manfred Geiler
*/
class SocketConnectRequestMessageListener implements ConnectRequestMessage.Listener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketConnectRequestMessageListener.class);

    private final RoutingTable routingTable;
    private final MessageSerializer<String> messageSerializer;
    private final URI localAddress;

    public SocketConnectRequestMessageListener(RoutingTable routingTable,
                                               MessageSerializer<String> messageSerializer,
                                               URI localAddress) {
        this.routingTable = routingTable;
        this.messageSerializer = messageSerializer;
        this.localAddress = localAddress;
    }

    @Override
    public void onConnectRequest(ConnectRequestMessage msg) {
        Party sender = msg.getSender();
        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {
            if (receiver instanceof SocketParty) {
                send((SocketParty) receiver, msg);
            }
        }
    }

    private void send(SocketParty receiver, ConnectRequestMessage msg) {
        try {
            SocketMessage socketMessage = SocketMessage.createConnectMsg(localAddress.toString(), msg.getModelName(),
                                                                         msg.getConnectParameters());
            String serializedMsg = messageSerializer.serialize(socketMessage);
            new SocketSender(receiver).send(serializedMsg);
        } catch (IOException e) {
            throw new RuntimeException("Error sending message to " + receiver, e);
        }
    }

}
