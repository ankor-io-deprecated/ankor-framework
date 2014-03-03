package at.irian.ankor.connector.socket;

import at.irian.ankor.msg.CloseMessage;
import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class SocketCloseMessageListener implements CloseMessage.Listener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SocketCloseMessageListener.class);

    private final SocketListener socketListener;

    public SocketCloseMessageListener(SocketListener socketListener) {
        this.socketListener = socketListener;
    }

    @Override
    public void onCloseMessage(CloseMessage msg) {
        Party party = msg.getPartyToClose();
        if (party instanceof SocketParty) {
            socketListener.removeSocketParty((SocketParty) party);
        }
    }
}
