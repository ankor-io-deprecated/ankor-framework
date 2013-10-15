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

    /**
     * @return true, if the current thread was register; false, if the current thread is already registered
     * @throws IllegalStateException if another thread was registered before
     */
    public boolean registerCurrentThread() {
        if (modelContext != null) {
            Thread previousDispatchThread = modelContext.getCurrentDispatchThread();
            Thread currentThread = Thread.currentThread();
            if (previousDispatchThread != null) {
                if (previousDispatchThread != currentThread) {
                    throw new IllegalStateException("ModelContext already being dispatched by another Thread: " + previousDispatchThread);
                }
                return false;
            } else {
                modelContext.setCurrentDispatchThread(Thread.currentThread());
                return true;
            }
        }
        return false;
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
                //LOG.warn("access to ModelContext from a non-dispatching thread");
            }
        }
    }

}
