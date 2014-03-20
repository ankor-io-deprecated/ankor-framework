package at.irian.ankor.switching.connector;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ThreadLocalHandlerScopeContext implements HandlerScopeContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleHandlerScopeContext.class);

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
