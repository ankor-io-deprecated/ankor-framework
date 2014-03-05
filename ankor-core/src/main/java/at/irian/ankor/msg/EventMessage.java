package at.irian.ankor.msg;

import at.irian.ankor.event.source.Source;
import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public abstract class EventMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(EventMessage.class);

    private final Source eventSource;

    protected EventMessage(Party sender, Source eventSource) {
        super(sender);
        this.eventSource = eventSource;
    }

    public abstract EventMessage withSender(Party sender);

    public Source getEventSource() {
        return eventSource;
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
