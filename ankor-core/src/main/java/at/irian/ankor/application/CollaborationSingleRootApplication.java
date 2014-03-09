package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public abstract class CollaborationSingleRootApplication extends SimpleSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CollaborationSingleRootApplication.class);

    public static final String MODEL_INSTANCE_ID_PARAM = CollaborationSingleRootApplication.class.getName() + ".MODEL_INSTANCE_ID";

    private final Map<String, Object> instanceMap = new ConcurrentHashMap<String, Object>();

    protected CollaborationSingleRootApplication(String applicationName, String modelName) {
        super(applicationName, modelName);
    }

    @Override
    public Object lookupModel(Map<String, Object> connectCriteria) {
        String instanceId = (String) connectCriteria.get(MODEL_INSTANCE_ID_PARAM);
        if (instanceId == null) {
            return null;
        } else {
            return instanceMap.get(instanceId);
        }
    }

    @Override
    public Object createModel(Ref rootRef) {
        return null;
    }

    @Override
    public void releaseModel(Object model) {

    }
}
