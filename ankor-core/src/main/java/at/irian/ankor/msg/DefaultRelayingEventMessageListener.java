package at.irian.ankor.msg;

import at.irian.ankor.event.source.PartySource;
import at.irian.ankor.msg.party.Party;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class DefaultRelayingEventMessageListener implements AbstractEventMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultRelayingEventMessageListener.class);

    private final RoutingTable routingTable;
    private final MessageBus messageBus;

    public DefaultRelayingEventMessageListener(RoutingTable routingTable, MessageBus messageBus) {
        this.routingTable = routingTable;
        this.messageBus = messageBus;
    }

    @Override
    public void onEventMessage(AbstractEventMessage msg) {

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

    private void forwardToOtherConnectedParties(AbstractEventMessage msg, Party originalSender, Party relayingParty) {
        Collection<Party> otherParties = routingTable.getConnectedParties(relayingParty);
        boolean partiesToForward = false;
        for (Party otherParty : otherParties) {
            if (otherParty.equals(originalSender)) {
                // do not relay back to original sender
            } else {
                partiesToForward = true;
                break;
            }
        }

        if (partiesToForward) {
            LOG.debug("Forwarding message {} originally sent by {} - forward sender is {}", msg, originalSender, relayingParty);
            messageBus.broadcast(msg.withSender(relayingParty));
        }
    }

}
