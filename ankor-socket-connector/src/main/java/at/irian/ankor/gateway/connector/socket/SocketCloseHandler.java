package at.irian.ankor.gateway.connector.socket;

import at.irian.ankor.gateway.handler.CloseHandler;
import at.irian.ankor.gateway.party.SocketParty;

/**
 * @author Manfred Geiler
 */
public class SocketCloseHandler implements CloseHandler<SocketParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketCloseHandler.class);

    @Override
    public void closeParty(SocketParty party) {
        LOG.info("Closing {}", party);
        // nothing to do at the moment - reserved for later: unregister party as "known socket party", etc.
    }
}
