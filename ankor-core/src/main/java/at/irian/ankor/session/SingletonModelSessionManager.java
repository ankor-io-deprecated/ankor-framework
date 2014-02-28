package at.irian.ankor.session;

/**
 * @author Manfred Geiler
 */
public class SingletonModelSessionManager implements ModelSessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelSessionManager.class);

    private final ModelSession modelSession;
    private String modelSessionId;

    public SingletonModelSessionManager(String modelSessionId, ModelSession modelSession) {
        this.modelSession = modelSession;
        this.modelSessionId = modelSessionId;
    }

    @Override
    public ModelSession getOrCreate(String modelSessionId) {
        if (modelSessionId != null) {
            if (this.modelSessionId != null) {
                if (!this.modelSessionId.equals(modelSessionId)) {
                    throw new IllegalStateException("wrong modelSession id " + modelSessionId + " - expected " + this.modelSessionId);
                }
            } else {
                this.modelSessionId = modelSessionId;
            }
        }
        return modelSession;
    }

    @Override
    public void invalidate(ModelSession modelSession) {
        modelSession.close();
    }
}
