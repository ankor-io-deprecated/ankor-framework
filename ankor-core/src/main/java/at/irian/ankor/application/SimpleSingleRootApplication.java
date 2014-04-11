package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Manfred Geiler
 */
public abstract class SimpleSingleRootApplication extends BaseApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSingleRootApplication.class);

    private final String modelName;
    private final Set<Object> modelRoots = new CopyOnWriteArraySet<Object>();

    public SimpleSingleRootApplication(String applicationName, String modelName) {
        super(applicationName);
        this.modelName = modelName;
    }

    @Override
    public boolean supportsModel(String modelName) {
        return this.modelName.equals(modelName);
    }

    @Override
    public final Object lookupModel(String modelName, Map<String, Object> connectCriteria) {
        checkModelName(modelName);
        return lookupModel(connectCriteria);
    }

    @Override
    public final Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        checkModelName(modelName);
        Object modelRoot = createModel(refContext.refFactory().ref(modelName), connectParameters);
        modelRoots.add(modelRoot);
        return modelRoot;
    }

    @Override
    public final void releaseModel(String modelName, Object modelRoot) {
        checkModelName(modelName);
        releaseModel(modelRoot);
        modelRoots.remove(modelRoot);
    }

    private void checkModelName(String modelName) {
        if (!this.modelName.equals(modelName)) {
            throw new IllegalArgumentException("Unexpected model name " + modelName + " - expected: " + this.modelName);
        }
    }

    public Object lookupModel(Map<String, Object> connectCriteria) {
        return null;
    }

    @Override
    public void shutdown() {
        for (Object modelRoot : modelRoots) {
            releaseModel(modelRoot);
        }
        modelRoots.clear();
    }

    public abstract Object createModel(Ref rootRef, Map<String, Object> connectParameters);

    public void releaseModel(Object model) {
        
    }

}
