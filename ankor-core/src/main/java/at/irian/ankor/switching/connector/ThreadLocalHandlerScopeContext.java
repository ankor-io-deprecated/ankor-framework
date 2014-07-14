package at.irian.ankor.switching.connector;

import java.util.Map;

/**
 * {@link HandlerScopeContext} that uses a {@link ThreadLocal} for storing worker-local values.
 * This implementation is meant for {@link at.irian.ankor.switching.Switchboard Switchboard} implementations that use
 * dedicated worker threads (e.g. based on {@link java.util.concurrent.ExecutorService}). This implementation is
 * <em>not</em> suitable for a {@link at.irian.ankor.switching.Switchboard Switchboards} based on an actor system,
 * because then the {@link HandlerScopeContext} should rather be based on the worker actor than the worker thread.
 *
 * @author Manfred Geiler
 * @see at.irian.ankor.switching.connector.SimpleHandlerScopeContext
 */
public class ThreadLocalHandlerScopeContext implements HandlerScopeContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ThreadLocalHandlerScopeContext.class);

    private static final ThreadLocal<HandlerScopeContext> THREAD_LOCAL = new ThreadLocal<HandlerScopeContext>() {
        @Override
        protected HandlerScopeContext initialValue() {
            return new SimpleHandlerScopeContext();
        }
    };

    @Override
    public Map<String, Object> getAttributes() {
        return THREAD_LOCAL.get().getAttributes();
    }
}
