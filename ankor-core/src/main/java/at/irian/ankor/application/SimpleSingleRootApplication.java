package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public abstract class SimpleSingleRootApplication<M> extends BaseApplication {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSingleRootApplication.class);

    private final String modelName;
    private final Map<String, ApplicationInstance> instanceMap = new ConcurrentHashMap<String, ApplicationInstance>();

    public SimpleSingleRootApplication(String applicationName, String modelName) {
        super(applicationName);
        this.modelName = modelName;
    }

    protected abstract M createRoot(Ref rootRef);

    protected void beforeInitInstance(String instanceId, RefContext refContext) {}

    protected void afterInitInstance(String instanceId, RefContext refContext, M root) {}

    protected void beforeReleaseInstance(String instanceId, M root) {}

    protected void afterReleaseInstance(String instanceId) {}

    @Override
    public ApplicationInstance getApplicationInstance(Map<String, Object> connectParameters) {

        ApplicationInstance instance;

        String instanceId = (String) connectParameters.get(APPLICATION_INSTANCE_ID_PARAM);
        if (instanceId == null) {
            instanceId = UUID.randomUUID().toString();
            instance = createInstance(instanceId);
            instanceMap.put(instanceId, instance);
        } else {
            instance = instanceMap.get(instanceId);
            if (instance == null) {
                instance = createInstance(instanceId);
                instanceMap.put(instanceId, instance);
            }
        }

        return instance;
    }


    protected ApplicationInstance createInstance(final String instanceId) {
        LOG.info("Creating new application instance with id '{}'", instanceId);
        return new ApplicationInstance() {

            private M root;

            @Override
            public void init(RefContext refContext) {
                beforeInitInstance(instanceId, refContext);
                Ref rootRef = refContext.refFactory().ref(modelName);
                this.root = createRoot(rootRef);
                afterInitInstance(instanceId, refContext, root);
            }

            @Override
            public Set<String> getKnownRootNames() {
                return Collections.singleton(modelName);
            }

            @Override
            public Object getModelRoot(String rootVarName) {
                return this.root;
            }

            @Override
            public void setModelRoot(String rootVarName, Object bean) {
                throw new UnsupportedOperationException("SimpleSingleRootApplication does not support custom root beans");
            }

            @Override
            public void release() {
                beforeReleaseInstance(instanceId, root);
                this.root = null;
                afterReleaseInstance(instanceId);

                instanceMap.remove(instanceId);
                LOG.info("Application instance with id '{}' was released", instanceId);
            }
        };
    }
}
