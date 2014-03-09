package at.irian.ankor.session;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Manfred Geiler
 */
public class DefaultModelSessionManager implements ModelSessionManager {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelSessionManager.class);

    private final Map<String, ModelSession> modelSessionIdMap = new HashMap<String, ModelSession>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void add(ModelSession modelSession) {
        lock.writeLock().lock();
        try {
            modelSessionIdMap.put(modelSession.getId(), modelSession);
            LOG.debug("New ModelSession {} added", modelSession);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ModelSession findByModelRoot(Object modelRoot) {
        lock.readLock().lock();
        try {
            //todo  we should cache modelRoot --> modelSession by means of an IdentityHashMap
            for (ModelSession modelSession : modelSessionIdMap.values()) {
                for (String modelName : modelSession.getModelNames()) {
                    Object mr = modelSession.getModelRoot(modelName);
                    if (mr == modelRoot) {
                        return modelSession;
                    }
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public ModelSession getById(String modelSessionId) {
        if (modelSessionId == null) {
            throw new NullPointerException("modelSessionId may not be null");
        }
        lock.readLock().lock();
        try {
            return modelSessionIdMap.get(modelSessionId);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(ModelSession modelSession) {
        lock.writeLock().lock();
        try {
            modelSessionIdMap.remove(modelSession.getId());
            LOG.debug("ModelSession {} removed", modelSession);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
