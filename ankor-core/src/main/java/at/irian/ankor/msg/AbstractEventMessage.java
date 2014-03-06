package at.irian.ankor.msg;

import at.irian.ankor.event.source.Source;
import at.irian.ankor.msg.party.Party;

/**
 * @author Manfred Geiler
 */
public abstract class AbstractEventMessage extends Message {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AbstractEventMessage.class);

    private final Source eventSource;

    protected AbstractEventMessage(Party sender, Source eventSource) {
        super(sender);
        this.eventSource = eventSource;
    }

    public abstract AbstractEventMessage withSender(Party sender);

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
        void onEventMessage(AbstractEventMessage msg);
    }

}
