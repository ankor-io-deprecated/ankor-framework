package at.irian.ankor.switching.routing;

import at.irian.ankor.switching.party.Party;
import at.irian.ankor.switching.party.SocketParty;

import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class FixedSocketRoutingLogic implements RoutingLogic {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedSocketRoutingLogic.class);

    private URI fixedReceiverAddress;

    public FixedSocketRoutingLogic(URI fixedReceiverAddress) {
        this.fixedReceiverAddress = fixedReceiverAddress;
    }

    @Override
    public Party findRoutee(Party sender, Map<String, Object> connectParameters) {
        return new SocketParty(fixedReceiverAddress, sender.getModelName());
    }
}
