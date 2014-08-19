package at.irian.ankor.event.source;

/**
 * The original source (initiator) of an event.
 * Every {@link at.irian.ankor.event.ModelEvent ModelEvent} must have a Source which is important for:
 * <ol>
 *     <li>giving the Ankor framework valuable information about where the event actually was fired (eg. locally vs. remote)</li>
 *     <li>having a way to determine "own" events and build a circuit-breaker that prevents endless event propagation</li>
 * </ol>
 *
 * @author Manfred Geiler
 */
public interface Source {
}
