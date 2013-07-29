package at.irian.ankor.ref.impl;

import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.session.Session;

/**
 * @author Manfred Geiler
 */
public interface RefContextImplementor extends RefContext {

    EventDelaySupport eventDelaySupport();

    EventDispatcher eventDispatcher();

    void setSession(Session session);
}
