package at.irian.ankor.application;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class SimpleApplication extends BaseApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleApplication.class);

    protected SimpleApplication(String name) {
        super(name);
    }

    @Override
    public Object lookupModel(String modelName, Map<String, Object> connectCriteria) {
        return null;
    }

}
