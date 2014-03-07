package at.irian.ankor.switching.connector.socket;

import at.irian.ankor.switching.handler.CloseHandler;
import at.irian.ankor.switching.party.SocketParty;

/**
 * @author Manfred Geiler
 */
public class SocketCloseHandler implements CloseHandler<SocketParty> {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketCloseHandler.class);

    @Override
    public void closeConnector(SocketParty party) {
        LOG.info("Closing {}", party);
        // nothing to do at the moment - reserved for later: unregister party as "known socket party", etc.
    }
}
