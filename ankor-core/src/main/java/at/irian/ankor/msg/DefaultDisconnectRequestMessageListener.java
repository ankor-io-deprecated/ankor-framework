package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;
import at.irian.ankor.msg.party.SystemParty;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class DefaultDisconnectRequestMessageListener implements DisconnectRequestMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultDisconnectRequestMessageListener.class);

    private final RoutingTable routingTable;
    private final MessageBus messageBus;

    public DefaultDisconnectRequestMessageListener(RoutingTable routingTable, MessageBus messageBus) {
        this.routingTable = routingTable;
        this.messageBus = messageBus;
    }

    @Override
    public void onDisconnectRequest(DisconnectRequestMessage msg) {
        Party sender = msg.getSender();
        LOG.info("Disconnect message received from {}", sender);

        Collection<Party> receivers = routingTable.getConnectedParties(msg.getSender());

        LOG.debug("Disconnecting {} from all other parties", sender);
        routingTable.disconnectAll(msg.getSender());

        for (Party receiver : receivers) {
            if (!routingTable.hasConnectedParties(receiver)) {
                LOG.debug("Requesting close for orphaned party {}", receiver);
                messageBus.broadcast(new CloseRequestMessage(SystemParty.getInstance(), receiver));
            }
        }

    }
}
