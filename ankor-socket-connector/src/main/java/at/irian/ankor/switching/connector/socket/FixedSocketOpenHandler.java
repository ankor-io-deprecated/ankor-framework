package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.switching.handler.OpenHandler;
import at.irian.ankor.switching.party.Party;
import at.irian.ankor.switching.party.SocketParty;

import java.net.URI;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class FixedSocketOpenHandler implements OpenHandler {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedSocketOpenHandler.class);

    private URI fixedReceiverAddress;

    public FixedSocketOpenHandler(URI fixedReceiverAddress) {
        this.fixedReceiverAddress = fixedReceiverAddress;
    }

    @Override
    public Party lookup(Party sender, Map<String, Object> connectParameters) {
        return new SocketParty(fixedReceiverAddress, sender.getModelName());
    }
}
