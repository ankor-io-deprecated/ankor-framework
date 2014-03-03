package at.irian.ankor.session;

import at.irian.ankor.application.ApplicationInstance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Manfred Geiler
 */
public class DefaultModelSessionManager implements ModelSessionManager {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelSessionManager.class);

    private final ModelSessionFactory modelSessionFactory;
    private final Map<ApplicationInstance, ModelSession> appInstanceMap = new ConcurrentHashMap<ApplicationInstance, ModelSession>();
    private final Map<String, ModelSession> modelSessionIdMap = new ConcurrentHashMap<String, ModelSession>();
    private final Lock lock = new ReentrantLock();

    public DefaultModelSessionManager(ModelSessionFactory modelSessionFactory) {
        this.modelSessionFactory = modelSessionFactory;
    }

    @Override
    public ModelSession getOrCreate(ApplicationInstance applicationInstance) {
        if (applicationInstance == null) {
            throw new NullPointerException("applicationInstance may not be null");
        }
        ModelSession modelSession = appInstanceMap.get(applicationInstance);
        if (modelSession == null) {
            lock.lock();
            try {
                modelSession = appInstanceMap.get(applicationInstance);
                if (modelSession == null) {
                    modelSession = modelSessionFactory.createModelSession(applicationInstance);
                    appInstanceMap.put(applicationInstance, modelSession);
                    modelSessionIdMap.put(modelSession.getId(), modelSession);
                }
            } finally {
                lock.unlock();
            }
        }
        return modelSession;
    }

    @Override
    public ModelSession getById(String modelSessionId) {
        if (modelSessionId == null) {
            throw new NullPointerException("modelSessionId may not be null");
        }
        return modelSessionIdMap.get(modelSessionId);
    }

    @Override
    public void invalidate(ModelSession modelSession) {
        lock.lock();
        try {
            modelSessionIdMap.remove(modelSession.getId());
            appInstanceMap.remove(modelSession.getApplicationInstance());
        } finally {
            lock.unlock();
        }

        try {
            modelSession.close();
        } catch (Exception e) {
            LOG.error("An exception was thrown while closing ModelSession " + modelSession, e);
        }
    }
}
