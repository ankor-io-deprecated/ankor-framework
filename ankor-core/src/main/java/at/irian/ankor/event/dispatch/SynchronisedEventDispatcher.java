package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;

/**
 * @author Manfred Geiler
 */
public class SynchronisedEventDispatcher extends SimpleEventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SynchronisedEventDispatcher.class);

    private final ModelSession modelSession;

    public SynchronisedEventDispatcher(ModelSession modelSession) {
        super(modelSession.getEventListeners());
        this.modelSession = modelSession;
    }

    @Override
    public void dispatch(ModelEvent event) {
        synchronized (modelSession) {
            DispatchThreadChecker dispatchThreadChecker = new DispatchThreadChecker(modelSession);
            boolean registered = dispatchThreadChecker.registerCurrentThread();
            try {
                super.dispatch(event);
            } finally {
                if (registered) {
                    dispatchThreadChecker.clear();
                }
            }
        }
    }

    @Override
    public void close() {
    }
}
