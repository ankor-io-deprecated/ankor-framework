package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class DispatchThreadChecker {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DispatchThreadChecker.class);

    private final DispatchThreadAware modelSession;

    public DispatchThreadChecker(ModelSession modelSession) {
        if (modelSession instanceof DispatchThreadAware) {
            this.modelSession = (DispatchThreadAware) modelSession;
        } else {
            this.modelSession = null;
        }
    }

    /**
     * @return true, if the current thread was register; false, if the current thread is already registered
     * @throws IllegalStateException if another thread was registered before
     */
    public boolean registerCurrentThread() {
        if (modelSession != null) {
            Thread previousDispatchThread = modelSession.getCurrentDispatchThread();
            Thread currentThread = Thread.currentThread();
            if (previousDispatchThread != null) {
                if (previousDispatchThread != currentThread) {
                    throw new IllegalStateException("ModelSession " + modelSession + " already being dispatched by another Thread: " + previousDispatchThread);
                }
                return false;
            } else {
                modelSession.setCurrentDispatchThread(Thread.currentThread());
                return true;
            }
        }
        return false;
    }

    public void clear() {
        if (modelSession != null) {
            modelSession.setCurrentDispatchThread(null);
        }
    }

    public void check() {
        if (modelSession != null) {
            if (modelSession.getCurrentDispatchThread() != Thread.currentThread()) {
                throw new IllegalStateException("Access to ModelSession " + modelSession + " from a non-dispatching thread " + Thread.currentThread() + " - expected " + modelSession.getCurrentDispatchThread());
                //LOG.warn("access to ModelSession from a non-dispatching thread");
            }
        }
    }

}
