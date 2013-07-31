package at.irian.ankor.event.dispatch;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public class SynchronisedEventDispatcherFactory implements EventDispatcherFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SynchronisedEventDispatcherFactory.class);

    @Override
    public EventDispatcher createFor(ModelContext modelContext) {
        return new SynchronisedEventDispatcher(modelContext);
    }

    @Override
    public void close() {
    }
}
