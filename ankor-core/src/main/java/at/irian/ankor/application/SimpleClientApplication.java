package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.Map;

/**
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

    @Override
    public boolean supportsModel(String modelName) {
        return true;
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectCriteria) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseModel(String modelName, Object model) {
    }

    @Override
    public void shutdown() {
    }
}
