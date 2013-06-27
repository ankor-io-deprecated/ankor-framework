package at.irian.ankor.ref.impl;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public interface RefContextImplementor extends RefContext {

    EventListeners modelEventListeners();
    EventListeners globalEventListeners();
    Iterable<ModelEventListener> allEventListeners();

}
