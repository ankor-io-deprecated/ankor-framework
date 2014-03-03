package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public class EventMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EventMessage.class);

    public EventMessage(Party sender) {
        super(sender);
    }

    @Override
    public boolean isAppropriateListener(MessageListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(MessageListener listener) {
        ((Listener)listener).onEventMessage(this);
    }

    public interface Listener extends MessageListener {
        void onEventMessage(EventMessage msg);
    }

}
