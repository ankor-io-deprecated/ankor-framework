package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public abstract class CollaborationSingleRootApplication extends SimpleSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CollaborationSingleRootApplication.class);

    public static final String MODEL_INSTANCE_ID_PARAM = "at.irian.ankor.MODEL_INSTANCE_ID";

    private final Map<String, Object> instanceMap = new ConcurrentHashMap<String, Object>();

    protected CollaborationSingleRootApplication(String applicationName, String modelName) {
        super(applicationName, modelName);
    }

    protected Map<String, Object> getModelInstanceMap() {
        return instanceMap;
    }

    @Override
    public Object lookupModel(Map<String, Object> connectParameters) {
        String instanceId = (String) connectParameters.get(MODEL_INSTANCE_ID_PARAM);
        if (instanceId == null) {
            return null;
        } else {
            return instanceMap.get(instanceId);
        }
    }

    @Override
    public Object createModel(Ref rootRef, Map<String, Object> connectParameters) {
        Object modelRoot = doCreateModel(rootRef, connectParameters);
        String instanceId = (String) connectParameters.get(MODEL_INSTANCE_ID_PARAM);
        instanceMap.put(instanceId, modelRoot);
        return modelRoot;
    }

    @Override
    public void releaseModel(Object model) {
        Iterator<Object> iterator = instanceMap.values().iterator();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next == model) {
                iterator.remove();
            }
        }
    }

    public abstract Object doCreateModel(Ref rootRef, Map<String, Object> connectParameters);

}
