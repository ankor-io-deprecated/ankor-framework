package at.irian.ankor.event.dispatch;

/**
 * @author Manfred Geiler
 */
public interface DispatchThreadAware {

    void setCurrentDispatchThread(Thread dispatchThread);

    Thread getCurrentDispatchThread();
}
