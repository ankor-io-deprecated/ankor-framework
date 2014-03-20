package at.irian.ankor.switching.connector;

import java.util.HashMap;
import java.util.Map;

/**
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
