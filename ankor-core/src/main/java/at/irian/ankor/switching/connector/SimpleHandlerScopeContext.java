package at.irian.ankor.switching.connector;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple {@link HashMap}-based {@link HandlerScopeContext}.
 * {@link at.irian.ankor.switching.Switchboard Switchboard} implementations that are based on an actor system may use
 * this SimpleHandlerScopeContext and store one instance per actor. Since actors are single-threaded by design there
 * is no need for a thread-based {@link HandlerScopeContext} in that case.
 *
 * @author Manfred Geiler
 */
public class SimpleHandlerScopeContext implements HandlerScopeContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleHandlerScopeContext.class);

    private final Map<String, Object> attributes = new HashMap<String, Object>();

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
}
