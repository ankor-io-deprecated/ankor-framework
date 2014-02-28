package at.irian.ankor.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class DefaultModelSessionManager implements ModelSessionManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelSessionManager.class);

    private final Map<String, ModelSession> modelSessionMap = new ConcurrentHashMap<String, ModelSession>();
    private final ModelSessionFactory modelSessionFactory;

    public DefaultModelSessionManager(ModelSessionFactory modelSessionFactory) {
        this.modelSessionFactory = modelSessionFactory;
    }

    @Override
    public ModelSession getOrCreate(String modelSessionId) {
        if (modelSessionId == null) {
            throw new NullPointerException("modelSessionId may not be null");
        }
        ModelSession modelSession = modelSessionMap.get(modelSessionId);
        if (modelSession == null) {
            synchronized (modelSessionMap) {
                modelSession = modelSessionMap.get(modelSessionId);
                if (modelSession == null) {
                    modelSession = modelSessionFactory.createModelSession(modelSessionId);
                    modelSessionMap.put(modelSessionId, modelSession);
                }
            }
        }
        return modelSession;
    }

    @Override
    public void invalidate(ModelSession modelSession) {
        synchronized (modelSessionMap) {
            modelSession = modelSessionMap.remove(modelSession.getId());
            modelSession.close();
        }
    }
}
