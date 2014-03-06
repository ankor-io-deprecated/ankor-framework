package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

/**
 * This message notifies all listeners that the sender is willing to logically disconnect from all other parties.
 *
 * @author Manfred Geiler
 */
public class DisconnectRequestMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DisconnectRequestMessage.class);

    public DisconnectRequestMessage(Party sender) {
        super(sender);
    }

    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onDisconnectRequest(this);
    }

    public interface Listener extends MessageListener {
        void onDisconnectRequest(DisconnectRequestMessage msg);
    }

}

