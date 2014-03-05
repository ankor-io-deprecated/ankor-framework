package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;
import at.irian.ankor.msg.party.SystemParty;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class DefaultDisconnectMessageListener implements DisconnectMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultDisconnectMessageListener.class);

    private final RoutingTable routingTable;
    private final MessageBus messageBus;

    public DefaultDisconnectMessageListener(RoutingTable routingTable, MessageBus messageBus) {
        this.routingTable = routingTable;
        this.messageBus = messageBus;
    }

    @Override
    public void onDisconnectMessage(DisconnectMessage msg) {
        Party sender = msg.getSender();
        LOG.info("Disconnect message received from {}", sender);

        Collection<Party> receivers = routingTable.getConnectedParties(msg.getSender());

        routingTable.disconnectAll(msg.getSender());

        for (Party receiver : receivers) {
            if (!routingTable.hasConnectedParties(receiver)) {
                messageBus.broadcast(new CloseMessage(SystemParty.getInstance(), receiver));
            }
        }

    }
}
