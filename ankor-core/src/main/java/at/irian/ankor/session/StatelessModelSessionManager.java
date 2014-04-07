package at.irian.ankor.session;

import at.irian.ankor.application.Application;

import java.util.Collections;

/**
 * @author Manfred Geiler
 */
public class StatelessModelSessionManager implements ModelSessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatelessModelSessionManager.class);

    private final ModelSessionFactory modelSessionFactory;
    private final Application application;

    public StatelessModelSessionManager(ModelSessionFactory modelSessionFactory, Application application) {
        this.modelSessionFactory = modelSessionFactory;
        this.application = application;
    }

    @Override
    public ModelSession findByModelRoot(Object modelRoot) {
        return null;
    }

    @Override
    public ModelSession getById(String modelSessionId) {
        ModelSession modelSession = modelSessionFactory.createModelSession();
        ((DefaultModelSession)modelSession).setId(modelSessionId);
        for (String modelName : application.getKnownModelNames()) {
            modelSession.setModelRoot(modelName,
                                      application.createModel(modelName,
                                                              Collections.<String,Object>emptyMap(),
                                                              modelSession.getRefContext()));
        }
        return modelSession;
    }

    @Override
    public void add(ModelSession modelSession) {
    }

    @Override
    public void remove(ModelSession modelSession) {
    }

    @Override
    public void close() {
    }

}
