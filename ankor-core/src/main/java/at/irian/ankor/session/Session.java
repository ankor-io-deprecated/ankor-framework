package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.ref.RefContext;

/**
 * @author Manfred Geiler
 */
public interface Session {

    String getId();

    boolean isActive();

    void start();

    void close();

    ModelContext getModelContext();

    RefContext getRefContext();

    EventDispatcher getEventDispatcher();

}
