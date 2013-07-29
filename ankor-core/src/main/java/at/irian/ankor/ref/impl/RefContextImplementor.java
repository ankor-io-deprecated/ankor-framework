package at.irian.ankor.ref.impl;

import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public interface RefContextImplementor extends RefContext {

    EventDelaySupport eventDelaySupport();

}
