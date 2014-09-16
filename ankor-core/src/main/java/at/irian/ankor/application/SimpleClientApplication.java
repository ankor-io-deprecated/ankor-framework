package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Application implementation for Java-based Ankor clients.
 *
 * @author Manfred Geiler
 */
public class SimpleClientApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleClientApplication.class);

    private final String name;

    public SimpleClientApplication(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Clients do not know about model names...
     * @return always an empty set
     */
    @Override
    public Set<String> getKnownModelNames() {
        return Collections.emptySet();
    }

    /**
     * Clients always have a state.
     * @return always false
     */
    @Override
    public boolean isStateless() {
        return false;
    }

    /**
     * Not applicable for clients.
     * @return (nothing)
     * @throws UnsupportedOperationException always
     */
    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectCriteria) {
        throw new UnsupportedOperationException();
    }

    /**
     * Not applicable for clients.
     * @return (nothing)
     * @throws UnsupportedOperationException always
     */
    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        throw new UnsupportedOperationException();
    }

    /**
     * Does nothing.
     */
    @Override
    public void releaseModel(String modelName, Object model) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void shutdown() {
    }
}
