package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class OrphanMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectMessage.class);

    private final Party orphanedParty;

    public OrphanMessage(Party sender, Party orphanedParty) {
        super(sender);
        this.orphanedParty = orphanedParty;
    }

    public Party getOrphanedParty() {
        return orphanedParty;
    }

    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onOrphanMessage(this);
    }

    public interface Listener extends MessageListener {
        void onOrphanMessage(OrphanMessage msg);
    }

}

