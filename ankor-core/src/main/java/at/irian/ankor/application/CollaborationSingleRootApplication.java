package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Convenient base class for Ankor applications that support model instance sharing (i.e. collaboration).
 *
 * Like {@link SimpleSingleRootApplication} this implementation supports an arbitrary number of model instances
 * that all have the same model root name (typically called "root"). In addition to that a client may reconnect to
 * an existing model instance by providing a "model instance id".
 *
 * @see #MODEL_INSTANCE_ID_PARAM
 * @author Manfred Geiler
 */
public abstract class CollaborationSingleRootApplication extends SimpleSingleRootApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CollaborationSingleRootApplication.class);

    /**
     * Connect parameter key for providing the "model instance id" to connect to.
     */
    public static final String MODEL_INSTANCE_ID_PARAM = "at.irian.ankor.MODEL_INSTANCE_ID";

    private final Map<String, Object> instanceMap = new ConcurrentHashMap<String, Object>();

    /**
     * @param applicationName  Name of this Ankor application
     * @param modelName        Name of the model root (typically "root")
     */
    protected CollaborationSingleRootApplication(String applicationName, String modelName) {
        super(applicationName, modelName);
    }

    @SuppressWarnings("UnusedDeclaration")
    protected Map<String, Object> getModelInstanceMap() {
        return instanceMap;
    }

    /**
     * Gets the "model instance id" from the given connect parameters map with the key {@link #MODEL_INSTANCE_ID_PARAM}
     * and looks up the associated model instance in the internal model instance map.
     * @param connectParameters  connect parameter map
     * @return model instance with the given "model instance id" or null if no id was given or a model instance with
     *         that id does not (yet) exist
     */
    @Override
    public final Object lookupModel(Map<String, Object> connectParameters) {
        String instanceId = (String) connectParameters.get(MODEL_INSTANCE_ID_PARAM);
        if (instanceId == null) {
            return null;
        } else {
            return instanceMap.get(instanceId);
        }
    }

    /**
     * Creates a new model instance (by delegating to {@link #doCreateModel(at.irian.ankor.ref.Ref, java.util.Map)} and
     * then stores the newly created model instance in the internal model instance map. The model instance is
     * stored with the "model instance id" that was given in the connect parameters under the key {@link #MODEL_INSTANCE_ID_PARAM}.
     * @return the newly created model instance
     */
    @Override
    public final Object createModel(Ref rootRef, Map<String, Object> connectParameters) {
        Object modelRoot = doCreateModel(rootRef, connectParameters);
        String instanceId = (String) connectParameters.get(MODEL_INSTANCE_ID_PARAM);
        if (instanceId != null) {
            instanceMap.put(instanceId, modelRoot);
        }
        return modelRoot;
    }

    /**
     * Disposes the given model from the internal model instance map.
     * Derived classed may override this method to do additional cleanup prior to disposing the model instance.
     * @param model  model instance
     */
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

    /**
     * Creates a new model instance (i.e. model root object).
     * @param rootRef             Ref to the new model root object
     * @param connectParameters   application-specific connect parameters
     * @return the newly created model instance
     */
    public abstract Object doCreateModel(Ref rootRef, Map<String, Object> connectParameters);

}
