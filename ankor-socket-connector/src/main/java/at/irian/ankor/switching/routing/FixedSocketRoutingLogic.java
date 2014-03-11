package at.irian.ankor.switching.routing;

import at.irian.ankor.switching.connector.socket.SocketModelAddress;

import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class FixedSocketRoutingLogic implements RoutingLogic {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedSocketRoutingLogic.class);

    private final URI fixedReceiverAddress;

    public FixedSocketRoutingLogic(URI fixedReceiverAddress) {
        this.fixedReceiverAddress = fixedReceiverAddress;
    }

    @Override
    public ModelAddress findRoutee(ModelAddress sender, Map<String, Object> connectParameters) {
        return new SocketModelAddress(fixedReceiverAddress, sender.getModelName());
    }
}
