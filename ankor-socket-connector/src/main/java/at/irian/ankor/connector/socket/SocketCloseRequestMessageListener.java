package at.irian.ankor.connector.socket;

import at.irian.ankor.msg.CloseRequestMessage;
import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class SocketCloseRequestMessageListener implements CloseRequestMessage.Listener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketCloseRequestMessageListener.class);

    @Override
    public void onCloseMessage(CloseRequestMessage msg) {
        Party party = msg.getPartyToClose();
        if (party instanceof SocketParty) {
            LOG.info("Closing {} as requested by {}", party, msg.getSender());
            // nothing to do at the moment - reserved for later: unregister party as "known socket party", etc.
        }
    }
}
