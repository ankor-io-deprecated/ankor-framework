package at.irian.ankor.gateway;

import at.irian.ankor.gateway.handler.CloseHandler;
import at.irian.ankor.gateway.handler.ConnectHandler;
import at.irian.ankor.gateway.handler.DeliverHandler;
import at.irian.ankor.gateway.routing.DefaultRoutingTable;
import at.irian.ankor.gateway.routing.RoutingTable;
import at.irian.ankor.gateway.msg.GatewayMsg;
import at.irian.ankor.gateway.party.Party;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class SimpleGateway implements Gateway {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleGateway.class);

    private ConnectHandler connectHandler;
    private final Map<Class<? extends Party>, DeliverHandler<? extends Party>> deliverHandlers = new ConcurrentHashMap<Class<? extends Party>, DeliverHandler<? extends Party>>();
    private final Map<Class<? extends Party>, CloseHandler<? extends Party>> closeHandlers = new ConcurrentHashMap<Class<? extends Party>, CloseHandler<? extends Party>>();
    private final RoutingTable routingTable = new DefaultRoutingTable();
    private volatile boolean started = false;

    @Override
    public void registerConnectHandler(ConnectHandler connectHandler) {
        if (this.connectHandler != null) {
            throw new IllegalStateException("ConnectHandler already registered - multiple handlers not supported");
        }
        this.connectHandler = connectHandler;
    }

    @Override
    public void unregisterConnectHandler() {
        if (this.connectHandler == null) {
            LOG.warn("ConnectHandler was not registered");
        }
        this.connectHandler = null;
    }

    @Override
    public void registerDisconnectHandler(Class<? extends Party> senderPartyType, CloseHandler<? extends Party> closeHandler) {
        if (closeHandlers.put(senderPartyType, closeHandler) != null) {
            throw new IllegalStateException("CloseHandler for party type " + senderPartyType.getName() + " already registered");
        }
    }

    @Override
    public void registerMessageDeliverer(Class<? extends Party> receiverPartyType, DeliverHandler<? extends Party> deliverHandler) {
        if (deliverHandlers.put(receiverPartyType, deliverHandler) != null) {
            throw new IllegalStateException("MessageDeliver for party type " + receiverPartyType.getName() + " already registered");
        }
    }

    @Override
    public void unregisterDisconnectHandler(Class<? extends Party> senderPartyType) {
        if (closeHandlers.remove(senderPartyType) == null) {
            LOG.warn("CloseHandler for party type " + senderPartyType.getName() + " was not registered");
        }
    }

    @Override
    public void unregisterMessageDeliverer(Class<? extends Party> receiverPartyType) {
        if (deliverHandlers.remove(receiverPartyType) == null) {
            LOG.warn("MessageDeliver for party type " + receiverPartyType.getName() + " was not registered");
        }
    }

    @Override
    public void routeConnect(Party sender, Map<String, Object> connectParameters) {
        checkStarted();

        Party receiver = connectHandler.findReceiver(sender, connectParameters);
        if (receiver != null) {
            connect(sender, receiver, connectParameters);
        } else {
            LOG.debug("Connect request from {} with params {} was not accepted", sender, connectParameters);
        }
    }

    @Override
    public void connect(Party sender, Party receiver, Map<String, Object> connectParameters) {
        checkStarted();
        if (!routingTable.connect(sender, receiver)) {
            throw new IllegalStateException("Already connected: " + sender + " and " + receiver);
        }
        //noinspection unchecked
        getDeliverHandler(receiver).open(sender, receiver, connectParameters);
    }

    @Override
    public void routeMessage(Party sender, GatewayMsg message) {
        checkStarted();
        Set<Party> alreadyDelivered = new HashSet<Party>(); // todo  optimze for 99% one-to-one routings (with Guava?)
        alreadyDelivered.add(sender);
        deliverRecursive(sender, sender, message, alreadyDelivered);
    }

    protected void deliverRecursive(Party originalSender,
                                    Party sender,
                                    GatewayMsg message,
                                    Set<Party> alreadyDelivered) {
        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {
            if (!alreadyDelivered.contains(receiver)) {
                deliverMessage(originalSender, receiver, message);
                alreadyDelivered.add(receiver);
                deliverRecursive(originalSender, receiver, message, alreadyDelivered);
            }
        }
    }

    @Override
    public void deliverMessage(Party sender, Party receiver, GatewayMsg message) {
        checkStarted();
        //noinspection unchecked
        getDeliverHandler(receiver).deliver(sender, receiver, message);
    }

    protected DeliverHandler getDeliverHandler(Party receiver) {
        Class<? extends Party> partyType = receiver.getClass();
        DeliverHandler deliverHandler = deliverHandlers.get(partyType);
        if (deliverHandler == null) {
            throw new IllegalStateException("No MessageDeliver found for party type " + partyType);
        }
        return deliverHandler;
    }

    @Override
    public void routeClose(Party sender) {
        checkStarted();

        LOG.debug("Closing {}", sender);
        closeParty(sender);

        Collection<Party> receivers = routingTable.getConnectedParties(sender);

        for (Party receiver : receivers) {

            close(sender, receiver);

            LOG.debug("Remove routing between {} and {}", sender, receiver);
            routingTable.disconnect(sender, receiver);

            if (!routingTable.hasConnectedParties(receiver)) {
                LOG.debug("Closing connection for orphaned {}", receiver);
                closeParty(receiver);
            }
        }
    }

    @Override
    public void close(Party sender, Party receiver) {
        checkStarted();
        //noinspection unchecked
        getDeliverHandler(receiver).close(sender, receiver);
    }

    protected void closeParty(Party party) {
        Class<? extends Party> partyType = party.getClass();
        CloseHandler closeHandler = closeHandlers.get(partyType);
        if (closeHandler != null) {
            //noinspection unchecked
            closeHandler.closeParty(party);
        } else {
            LOG.warn("No disconnect handler found for " + party);
        }
    }

    @Override
    public void start() {
        this.started = true;
    }

    @Override
    public void stop() {
        this.started = false;
    }

    private void checkStarted() {
        if (!started) {
            throw new IllegalStateException("Gateway not started");
        }
    }

}
