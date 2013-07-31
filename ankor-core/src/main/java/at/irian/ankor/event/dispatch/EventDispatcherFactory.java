package at.irian.ankor.event.dispatch;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public interface EventDispatcherFactory {

    EventDispatcher createFor(ModelContext modelContext);

    void close();
}
