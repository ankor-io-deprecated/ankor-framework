package at.irian.ankor.servlet.polling;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.source.Source;

/**
 * @author Manfred Geiler
 */
public class RequestFinishedEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RequestFinishedEvent.class);

    public RequestFinishedEvent(Source source) {
        super(source);
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((Listener)listener).processRequestFinished(this);
    }

    public interface Listener extends ModelEventListener {
        void processRequestFinished(RequestFinishedEvent requestFinishedEvent);
    }

}
