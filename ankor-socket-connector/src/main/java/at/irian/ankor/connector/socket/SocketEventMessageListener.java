package at.irian.ankor.connector.socket;

import at.irian.ankor.messaging.MessageSerializer;
import at.irian.ankor.msg.*;
import at.irian.ankor.msg.party.Party;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

/**
* @author Manfred Geiler
*/
class SocketEventMessageListener implements AbstractEventMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketEventMessageListener.class);

    private final RoutingTable routingTable;
    private final MessageSerializer<String> messageSerializer;
    private final URI localAddress;
    private final MessageBus messageBus;

    public SocketEventMessageListener(RoutingTable routingTable,
                                      MessageSerializer<String> messageSerializer,
                                      URI localAddress,
                                      MessageBus messageBus) {
        this.routingTable = routingTable;
        this.messageSerializer = messageSerializer;
        this.localAddress = localAddress;
        this.messageBus = messageBus;
    }

    @Override
    public void onEventMessage(AbstractEventMessage msg) {
        Party sender = msg.getSender();
        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {
            if (receiver instanceof SocketParty) {
                try {
                    send((SocketParty) receiver, msg);
                } catch (IOException e) {
                    LOG.error("Error sending msg {} to {} - closing logical connection to this party", msg, receiver);
                    messageBus.broadcast(new DisconnectRequestMessage(receiver));
                }
            }
        }
    }


    private void send(SocketParty receiver, AbstractEventMessage eventMessage) throws IOException {
        SocketMessage socketMessage;
        if (eventMessage instanceof ActionEventMessage) {
            socketMessage = SocketMessage.createActionMsg(localAddress.toString(),
                                                          ((ActionEventMessage) eventMessage).getProperty(),
                                                          ((ActionEventMessage) eventMessage).getAction());
        } else if (eventMessage instanceof ChangeEventMessage) {
            socketMessage = SocketMessage.createChangeMsg(localAddress.toString(),
                                                          ((ChangeEventMessage) eventMessage).getProperty(),
                                                          ((ChangeEventMessage) eventMessage).getChange());
        } else {
            throw new IllegalArgumentException("Unsupported event message type " + eventMessage.getClass().getName());
        }

        String serializedMsg = messageSerializer.serialize(socketMessage);

        new SocketSender(receiver).send(serializedMsg);
    }

}
