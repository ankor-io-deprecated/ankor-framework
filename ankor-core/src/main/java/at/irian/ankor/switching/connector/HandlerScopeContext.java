package at.irian.ankor.switching.connector;

import java.util.Map;

/**
 * Context for storing custom attributes in the handler scope.
 * Handler scope means, that there is exactly one instance of this context per worker (or actor) that calls the
 * handler. A handler is allowed to assume that the HandlerScopeContext is thread-safe. In no situaton the same
 * HandlerScopeContext instance is handed to multiple threads concurrently.
 * Therefore the HandlerScopeContext is the proper place for caching or pooling thread-unsafe data or helpers that
 * are not thread-safe. e.g. If a handler needs to makes use of a thread-unsafe serializer/deserializer
 * this HandlerScopeContext is the perfect place to cache an instance of this serializer/deserializer.
 *
 * @author Manfred Geiler
 */
public interface HandlerScopeContext {
    /**
     * @return a Map for storing worker-local data or helpers; handler implementors are encouraged to use unique keys
     *         for storing their values, so that different pluggable handlers won't have a name clash here
     */
    Map<String, Object> getAttributes();
}
