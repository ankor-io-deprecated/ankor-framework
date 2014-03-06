package at.irian.ankor.msg;

import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.msg.party.Party;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class DefaultRelayingEventMessageListener implements EventMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultRelayingEventMessageListener.class);

    private final RoutingTable routingTable;
    private final MessageBus messageBus;

    public DefaultRelayingEventMessageListener(RoutingTable routingTable, MessageBus messageBus) {
        this.routingTable = routingTable;
        this.messageBus = messageBus;
    }

    @Override
    public void onEventMessage(EventMessage msg) {

        Party sender = msg.getSender();
        Collection<Party> receivers = routingTable.getConnectedParties(sender);

        for (Party receiver : receivers) {

            if (receiver.equals(sender)) {
                LOG.error("Self-connected sender detected: {} - not forwarding msg {}", sender, msg);
                continue;
            }

            if (msg.getEventSource() instanceof PartySource) {
                Party eventSourceParty = ((PartySource) msg.getEventSource()).getParty();
                if (receiver.equals(eventSourceParty)) {
                    LOG.error("Circular routing detected: {} - not forwarding msg {}", receiver, msg);
                    continue;
                }
            }

            // propagate it to other connected parties
            forwardToOtherConnectedParties(msg, sender, receiver);
        }

    }

    private void forwardToOtherConnectedParties(EventMessage msg, Party originalSender, Party localModelParty) {
        Collection<Party> otherParties = routingTable.getConnectedParties(localModelParty);
        for (Party otherParty : otherParties) {
            if (otherParty.equals(originalSender)) {
                // do not relay back to original sender
            } else {
                LOG.debug("");
                messageBus.broadcast(msg.withSender(localModelParty));
            }
        }
    }

}
