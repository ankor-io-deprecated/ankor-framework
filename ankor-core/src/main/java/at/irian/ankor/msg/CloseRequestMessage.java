package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class CloseRequestMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CloseRequestMessage.class);

    private final Party partyToClose;

    public CloseRequestMessage(Party sender, Party partyToClose) {
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
        ((Listener)listener).onCloseRequest(this);
    }

    public interface Listener extends MessageListener {
        void onCloseRequest(CloseRequestMessage msg);
    }

}

