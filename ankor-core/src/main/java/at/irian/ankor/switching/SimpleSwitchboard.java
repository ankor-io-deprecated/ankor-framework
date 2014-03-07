package at.irian.ankor.switching;

import at.irian.ankor.switching.handler.CloseHandler;
import at.irian.ankor.switching.handler.OpenHandler;
import at.irian.ankor.switching.handler.SendHandler;
import at.irian.ankor.switching.routing.DefaultRoutingTable;
import at.irian.ankor.switching.routing.RoutingTable;
import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.party.Party;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class SimpleSwitchboard implements Switchboard {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSwitchboard.class);

    private OpenHandler openHandler;
    private final Map<Class<? extends Party>, SendHandler<? extends Party>> deliverHandlers = new ConcurrentHashMap<Class<? extends Party>, SendHandler<? extends Party>>();
    private final Map<Class<? extends Party>, CloseHandler<? extends Party>> closeHandlers = new ConcurrentHashMap<Class<? extends Party>, CloseHandler<? extends Party>>();
    private final RoutingTable routingTable = new DefaultRoutingTable();
    private volatile boolean started = false;

    @Override
    public void registerOpenHandler(OpenHandler openHandler) {
        if (this.openHandler != null) {
            throw new IllegalStateException("OpenHandler already registered - multiple handlers not supported");
        }
        this.openHandler = openHandler;
    }

    @Override
    public void unregisterOpenHandler() {
        if (this.openHandler == null) {
            LOG.warn("OpenHandler was not registered");
        }
        this.openHandler = null;
    }

    @Override
    public void registerCloseHandler(Class<? extends Party> senderPartyType, CloseHandler<? extends Party> closeHandler) {
        if (closeHandlers.put(senderPartyType, closeHandler) != null) {
            throw new IllegalStateException("CloseHandler for party type " + senderPartyType.getName() + " already registered");
        }
    }

    @Override
    public void registerSendHandler(Class<? extends Party> receiverPartyType, SendHandler<? extends Party> sendHandler) {
        if (deliverHandlers.put(receiverPartyType, sendHandler) != null) {
            throw new IllegalStateException("MessageDeliver for party type " + receiverPartyType.getName() + " already registered");
        }
    }

    @Override
    public void unregisterCloseHandler(Class<? extends Party> senderPartyType) {
        if (closeHandlers.remove(senderPartyType) == null) {
            LOG.warn("CloseHandler for party type " + senderPartyType.getName() + " was not registered");
        }
    }

    @Override
    public void unregisterSendHandler(Class<? extends Party> receiverPartyType) {
        if (deliverHandlers.remove(receiverPartyType) == null) {
            LOG.warn("MessageDeliver for party type " + receiverPartyType.getName() + " was not registered");
        }
    }

    @Override
    public void open(Party sender, Map<String, Object> connectParameters) {
        checkStarted();

        Party receiver = openHandler.lookup(sender, connectParameters);
        if (receiver != null) {
            connect(sender, receiver, connectParameters);
        } else {
            LOG.debug("Connect request from {} with params {} was not accepted", sender, connectParameters);
        }
    }

    protected void connect(Party sender, Party receiver, Map<String, Object> connectParameters) {
        checkStarted();
        if (!routingTable.connect(sender, receiver)) {
            throw new IllegalStateException("Already connected: " + sender + " and " + receiver);
        }
        //noinspection unchecked
        getDeliverHandler(receiver).deliverConnectRequest(sender, receiver, connectParameters);
    }

    @Override
    public void send(Party sender, EventMessage message) {
        checkStarted();
        Set<Party> alreadyDelivered = new HashSet<Party>(); // todo  optimze for 99% one-to-one routings (with Guava?)
        alreadyDelivered.add(sender);
        deliverRecursive(sender, sender, message, alreadyDelivered);
    }

    protected void deliverRecursive(Party originalSender,
                                    Party sender,
                                    EventMessage message,
                                    Set<Party> alreadyDelivered) {
        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {
            if (!alreadyDelivered.contains(receiver)) {
                deliver(originalSender, receiver, message);
                alreadyDelivered.add(receiver);
                deliverRecursive(originalSender, receiver, message, alreadyDelivered);
            }
        }
    }

    protected void deliver(Party sender, Party receiver, EventMessage message) {
        checkStarted();
        //noinspection unchecked
        getDeliverHandler(receiver).deliverEventMessage(sender, receiver, message);
    }

    protected SendHandler getDeliverHandler(Party receiver) {
        Class<? extends Party> partyType = receiver.getClass();
        SendHandler sendHandler = deliverHandlers.get(partyType);
        if (sendHandler == null) {
            throw new IllegalStateException("No MessageDeliver found for party type " + partyType);
        }
        return sendHandler;
    }

    @Override
    public void close(Party sender) {
        checkStarted();

        Collection<Party> receivers = routingTable.getConnectedParties(sender);
        for (Party receiver : receivers) {

            close(sender, receiver);

            if (!routingTable.hasConnectedParties(receiver)) {
                LOG.debug("Closing connection for orphaned {}", receiver);
                closeConnector(receiver);
            }
        }

        LOG.debug("Closing {}", sender);
        closeConnector(sender);
    }

    protected void close(Party sender, Party receiver) {
        checkStarted();

        LOG.debug("Send close request from {} to {}", sender, receiver);
        //noinspection unchecked
        getDeliverHandler(receiver).deliverCloseRequest(sender, receiver);

        LOG.debug("Remove routing between {} and {}", sender, receiver);
        routingTable.disconnect(sender, receiver);
    }

    protected void closeConnector(Party party) {
        Class<? extends Party> partyType = party.getClass();
        CloseHandler closeHandler = closeHandlers.get(partyType);
        if (closeHandler != null) {
            //noinspection unchecked
            closeHandler.closeConnector(party);
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
            throw new IllegalStateException("Switchboard not started");
        }
    }

}
