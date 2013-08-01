package at.irian.ankor.event.dispatch;

import at.irian.ankor.context.ModelContext;

/**
 * @author Manfred Geiler
 */
public class DispatchThreadChecker {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DispatchThreadChecker.class);

    private final DispatchThreadAware modelContext;

    public DispatchThreadChecker(ModelContext modelContext) {
        if (modelContext instanceof DispatchThreadAware) {
            this.modelContext = (DispatchThreadAware)modelContext;
        } else {
            this.modelContext = null;
        }
    }

    public void register() {
        if (modelContext != null) {
            Thread currentDispatchThread = modelContext.getCurrentDispatchThread();
            if (currentDispatchThread != null && currentDispatchThread != Thread.currentThread()) {
                throw new IllegalStateException("ModelContext already being dispatched by another Thread: " + currentDispatchThread);
            }
            modelContext.setCurrentDispatchThread(Thread.currentThread());
        }
    }

    public void clear() {
        if (modelContext != null) {
            modelContext.setCurrentDispatchThread(null);
        }
    }

    public void check() {
        if (modelContext != null) {
            if (modelContext.getCurrentDispatchThread() != Thread.currentThread()) {
                throw new IllegalStateException("access to ModelContext from a non-dispatching thread");
            }
        }
    }

}
