package at.irian.ankor.session;

import at.irian.ankor.application.ApplicationInstance;

/**
 * @author Manfred Geiler
 */
public class SingletonModelSessionManager implements ModelSessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelSessionManager.class);

    private final ModelSession modelSession;
    private ApplicationInstance applicationInstance;

    public SingletonModelSessionManager(ApplicationInstance applicationInstance, ModelSession modelSession) {
        this.modelSession = modelSession;
        this.applicationInstance = applicationInstance;
    }

    @Override
    public ModelSession getOrCreate(ApplicationInstance applicationInstance) {
        if (applicationInstance != null) {
            if (this.applicationInstance != null) {
                if (!this.applicationInstance.equals(applicationInstance)) {
                    throw new IllegalStateException("wrong applicationInstance id " + applicationInstance + " - expected " + this.applicationInstance);
                }
            } else {
                this.applicationInstance = applicationInstance;
            }
        }
        return modelSession;
    }

    @Override
    public ModelSession getById(String modelSessionId) {
        if (modelSession.getId().equals(modelSessionId)) {
            return modelSession;
        } else {
            return null;
        }
    }

    @Override
    public void invalidate(ModelSession modelSession) {
        modelSession.close();
        applicationInstance.release();
    }

    public ModelSession getModelSession() {
        return modelSession;
    }

    public ApplicationInstance getApplicationInstance() {
        return applicationInstance;
    }
}
