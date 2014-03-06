package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

import java.util.Map;

/**
 * This message notifies all listeners that the sender is willing to logically disconnect from all other parties.
 *
 * @author Manfred Geiler
 */
public class DisconnectMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConnectMessage.class);

    public DisconnectMessage(Party sender) {
        super(sender);
    }

    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onDisconnectMessage(this);
    }

    public interface Listener extends MessageListener {
        void onDisconnectMessage(DisconnectMessage msg);
    }

}

