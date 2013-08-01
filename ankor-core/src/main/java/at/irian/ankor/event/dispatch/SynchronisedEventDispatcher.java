package at.irian.ankor.event.dispatch;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class SynchronisedEventDispatcher extends SimpleEventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SynchronisedEventDispatcher.class);

    private final ModelContext modelContext;

    public SynchronisedEventDispatcher(ModelContext modelContext) {
        super(modelContext.getEventListeners());
        this.modelContext = modelContext;
    }

    @Override
    public void dispatch(ModelEvent event) {
        synchronized (modelContext) {
            DispatchThreadChecker dispatchThreadChecker = new DispatchThreadChecker(modelContext);
            dispatchThreadChecker.register();
            try {
                super.dispatch(event);
            } finally {
                dispatchThreadChecker.clear();
            }
        }
    }

    @Override
    public void close() {
    }
}
