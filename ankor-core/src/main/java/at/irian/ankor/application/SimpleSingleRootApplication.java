package at.irian.ankor.application;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Convenient Application implementation that supports an arbitrary number of model instances that all have the same
 * model root name (typically called "root").
 *
 * @author Manfred Geiler
 */
public abstract class SimpleSingleRootApplication extends BaseApplication {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleSingleRootApplication.class);

    private final String modelName;
    private final Set<Object> modelRoots = new CopyOnWriteArraySet<Object>();

    /**
     * @param applicationName  Name of this Ankor application
     * @param modelName        Name of the model root (typically "root")
     */
    protected SimpleSingleRootApplication(String applicationName, String modelName) {
        super(applicationName);
        this.modelName = modelName;
    }

    /**
     * @return a set with the model name as single element
     */
    @Override
    public Set<String> getKnownModelNames() {
        return Collections.singleton(this.modelName);
    }

    /**
     * Checks the given model name and then delegates to {@link #lookupModel(java.util.Map)}.
     * @throws java.lang.IllegalArgumentException if the given model name does not match this applications model name
     */
    @Override
    public final Object lookupModel(String modelName, Map<String, Object> connectCriteria) {
        checkModelName(modelName);
        return lookupModel(connectCriteria);
    }

    /**
     * Checks the given model name, then delegates to {@link #createModel(at.irian.ankor.ref.Ref, java.util.Map)}
     * and finally stores the given model instance in an internal map for later retrieval.
     *
     * @param modelName         name of model (must match this applications model name)
     * @param connectParameters (optional) connect parameters
     * @param refContext        RefContext for the corresponding ModelSession
     * @return newly created model root object
     * @throws java.lang.IllegalArgumentException if the given model name does not match this applications model name
     */
    @Override
    public final Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext) {
        checkModelName(modelName);
        Object modelRoot = createModel(refContext.refFactory().ref(modelName), connectParameters);
        if (!isStateless()) modelRoots.add(modelRoot);
        return modelRoot;
    }

    /**
     * Checks the given model name, then delegates to {@link #releaseModel(Object)} and finally disposes the
     * given model instance from memory.
     * @param modelName    name of model
     * @param modelRoot    root of model instance to free
     * @throws java.lang.IllegalArgumentException if the given model name does not match this applications model name
     */
    @Override
    public final void releaseModel(String modelName, Object modelRoot) {
        checkModelName(modelName);
        try {
            releaseModel(modelRoot);
        } finally {
            if (!isStateless()) modelRoots.remove(modelRoot);
        }
    }

    private void checkModelName(String modelName) {
        if (!this.modelName.equals(modelName)) {
            throw new IllegalArgumentException("Unexpected model name " + modelName + " - expected: " + this.modelName);
        }
    }

    /**
     * Always returns null. Derived classes may overwrite this method to support collaboration or concurrent device usage.
     * @return always null
     */
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

    /**
     * Does nothing. Derived classes may overwrite this method to implement additional cleanup prior to disposing
     * the given model instance from memory.
     */
    public void releaseModel(Object model) {
        
    }

}
