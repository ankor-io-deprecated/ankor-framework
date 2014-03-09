package at.irian.ankor.session;

/**
 * @author Manfred Geiler
 */
public class SingletonModelSessionManager implements ModelSessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelSessionManager.class);

    private ModelSession modelSession;

    @Override
    public void add(ModelSession modelSession) {
        if (this.modelSession != null) {
            throw new IllegalStateException("ModelSession already set");
        }
        this.modelSession = modelSession;
    }

    @Override
    public ModelSession findByModelRoot(Object modelRoot) {
        checkModelSession();
        for (String modelName : modelSession.getModelNames()) {
            Object mr = modelSession.getModelRoot(modelName);
            if (mr == modelRoot) {
                return modelSession;
            }
        }
        return null;
    }

    @Override
    public ModelSession getById(String modelSessionId) {
        checkModelSession();
        if (modelSession.getId().equals(modelSessionId)) {
            return modelSession;
        } else {
            return null;
        }
    }

    @Override
    public void remove(ModelSession modelSession) {
        checkModelSession();
        if (modelSession == this.modelSession) {
            this.modelSession = null;
        }
    }

    private void checkModelSession() {
        if (modelSession == null) {
            throw new IllegalStateException("No ModelSession");
        }
    }

    public ModelSession getModelSession() {
        return modelSession;
    }

}
