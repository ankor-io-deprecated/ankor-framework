package at.irian.ankor.event;

import at.irian.ankor.event.source.Source;

import java.util.EventObject;

/**
 * An event that happened (and was fired) within the context of a model.
 * Every ModelEvent has a {@link at.irian.ankor.event.source.Source} which is important for:
 * <ol>
 *     <li>giving the Ankor framework valuable information about where the event actually was fired (eg. locally vs. remote)</li>
 *     <li>having a way to determine "own" events and build a circuit-breaker that prevents endless event propagation</li>
 * </ol>
 *
 * The Ankor event system uses a double-dispatch strategy. Every ModelEvent knows which type of event listener
 * is able to process it's kind ({@link #isAppropriateListener(ModelEventListener)}) and is able to dispatch the
 * processing to the given (appropriate) listener instance ({@link #processBy(ModelEventListener)}).
 *
 * @author Manfred Geiler
 */
public abstract class ModelEvent extends EventObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEvent.class);

    protected ModelEvent(Source source) {
        super(source);
    }

    /**
     * @return the {@link at.irian.ankor.event.source.Source} of this event
     */
    @Override
    public Source getSource() {
        return (Source)super.getSource();
    }

    /**
     * @param listener  a {@link ModelEventListener} instance
     * @return true if the given listener is of a well-known type that is appropriate for processing this event
     */
    public abstract boolean isAppropriateListener(ModelEventListener listener);

    /**
     * Dispatch the processing of this event to the given listener instance
     * @param listener  a {@link ModelEventListener} instance
     */
    public abstract void processBy(ModelEventListener listener);
}
