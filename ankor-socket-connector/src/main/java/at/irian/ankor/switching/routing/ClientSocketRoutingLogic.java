package at.irian.ankor.switching.routing;

import at.irian.ankor.monitor.nop.NopRoutingTableMonitor;
import at.irian.ankor.switching.connector.socket.SocketModelAddress;

import java.net.URI;
import java.util.Collection;
import java.util.Map;

/**
 * RoutingLogic that always connects to an external directed SocketModelAddress.
 * This RoutingLogic is primarily meant for client nodes that do not themselves support connect requests from outside.
 *
 * @author Manfred Geiler
 */
public class ClientSocketRoutingLogic implements RoutingLogic {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientSocketRoutingLogic.class);

    private final URI fixedReceiverAddress;
    private final RoutingTable routingTable;

    public ClientSocketRoutingLogic(URI fixedReceiverAddress) {
        this.fixedReceiverAddress = fixedReceiverAddress;
        this.routingTable = new DefaultRoutingTable(new NopRoutingTableMonitor());
    }

    @Override
    public ModelAddress connect(ModelAddress sender, Map<String, Object> connectParameters) {
        SocketModelAddress receiver = new SocketModelAddress(fixedReceiverAddress, sender.getModelName());
        boolean success = routingTable.connect(sender, receiver);
        if (!success) {
            LOG.warn("Already connected: {} and {}", sender, receiver);
        }
        return receiver;
    }

    @Override
    public Collection<ModelAddress> getConnectedRoutees(ModelAddress sender) {
        return routingTable.getConnectedAddresses(sender);
    }

    @Override
    public Collection<ModelAddress> getAllConnectedRoutees() {
        return routingTable.getAllConnectedAddresses();
    }

    @Override
    public void disconnect(ModelAddress sender, ModelAddress receiver) {
        routingTable.disconnect(sender, receiver);
    }

    @Override
    public void init() {
    }

    @Override
    public void close() {
        routingTable.clear();
    }
}
