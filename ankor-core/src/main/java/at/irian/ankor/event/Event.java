package at.irian.ankor.event;

import at.irian.ankor.event.source.Source;

import java.util.EventObject;

/**
 * An event that happened (and was fired) in an Ankor application.
 * Events can occur within the context of a model or outside the scope of a model.
 * Every event has a {@link at.irian.ankor.event.source.Source} which is important for:
 * <ol>
 *     <li>giving the Ankor framework valuable information about where the event actually was fired (eg. locally vs. remote)</li>
 *     <li>having a way to determine "own" events and build a circuit-breaker that prevents endless event propagation</li>
 * </ol>
 *
 * The Ankor event system uses a double-dispatch strategy. Every event knows which type of event listener
 * is able to process it's kind ({@link #isAppropriateListener(EventListener)}) and is able to dispatch the
 * actual processing to the given (appropriate) listener instance ({@link #processBy(EventListener)}).
 *
 * @author Manfred Geiler
 */
public abstract class Event extends EventObject {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelEvent.class);

    protected Event(Source source) {
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
     * @param listener  an {@link EventListener} instance
     * @return true, if the given listener is of a well-known type and/or the given listener instance is appropriate
     *         for processing this event
     */
    public abstract boolean isAppropriateListener(EventListener listener);

    /**
     * Dispatch the processing of this event to the given listener instance
     * @param listener  an {@link EventListener} instance
     */
    public abstract void processBy(EventListener listener);
}
