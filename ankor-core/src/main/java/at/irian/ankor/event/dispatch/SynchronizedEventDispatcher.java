package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.Event;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class SynchronizedEventDispatcher implements EventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SynchronizedEventDispatcher.class);

    private final ModelSession modelSession;
    private final EventDispatcher delegateEventDispatcher;

    public SynchronizedEventDispatcher(ModelSession modelSession, EventDispatcher delegateEventDispatcher) {
        this.modelSession = modelSession;
        this.delegateEventDispatcher = delegateEventDispatcher;
    }

    @Override
    public void dispatch(Event event) {
        synchronized (modelSession) {
            DispatchThreadChecker dispatchThreadChecker = new DispatchThreadChecker(modelSession);
            boolean registered = dispatchThreadChecker.registerCurrentThread();
            try {
                delegateEventDispatcher.dispatch(event);
            } finally {
                if (registered) {
                    dispatchThreadChecker.clear();
                }
            }
        }
    }

    @Override
    public void close() {
        delegateEventDispatcher.close();
    }
}
