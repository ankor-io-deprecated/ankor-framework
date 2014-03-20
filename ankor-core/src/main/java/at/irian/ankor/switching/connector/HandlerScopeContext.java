package at.irian.ankor.switching.connector;

import java.util.Map;

/**
 * Context for storing custom attributes in the handler scope.
 * Handler scope means, that there is exactly one instance of this context per worker (or actor) that calls the
 * handler.
 *
 * @author Manfred Geiler
 */
public interface HandlerScopeContext {
    Map<String, Object> getAttributes();
}
