package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public abstract class SimpleSingleRootApplication extends BaseApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSingleRootApplication.class);

    private final String modelName;

    public SimpleSingleRootApplication(String applicationName, String modelName) {
        super(applicationName);
        this.modelName = modelName;
    }

    @Override
    public final Object lookupModel(String modelName, Map<String, Object> connectCriteria) {
        checkModelName(modelName);
        return lookupModel(connectCriteria);
    }

    @Override
    public final Object createModel(String modelName, RefContext refContext) {
        checkModelName(modelName);
        return createModel(refContext.refFactory().ref(modelName));
    }

    @Override
    public final void releaseModel(String modelName, Object modelRoot) {
        checkModelName(modelName);
        releaseModel(modelRoot);
    }

    private void checkModelName(String modelName) {
        if (!this.modelName.equals(modelName)) {
            throw new IllegalArgumentException("Unexpected model name " + modelName + " - expected: " + this.modelName);
        }
    }

    public Object lookupModel(Map<String, Object> connectCriteria) {
        return null;
    }

    public abstract Object createModel(Ref rootRef);

    public void releaseModel(Object model) {

    }

}
