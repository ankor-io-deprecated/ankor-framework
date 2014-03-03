package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class CloseMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectMessage.class);

    private final Party partyToClose;

    public CloseMessage(Party sender, Party partyToClose) {
        super(sender);
        this.partyToClose = partyToClose;
    }

    public Party getPartyToClose() {
        return partyToClose;
    }

    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onCloseMessage(this);
    }

    public interface Listener extends MessageListener {
        void onCloseMessage(CloseMessage msg);
    }

}

