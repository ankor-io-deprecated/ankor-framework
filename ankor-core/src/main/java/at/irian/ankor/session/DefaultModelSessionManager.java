package at.irian.ankor.session;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class DefaultModelSessionManager implements ModelSessionManager {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelSessionManager.class);

    private static final int INITIAL_CAPACITY = 100;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int DEFAULT_CONCURRENCY_LEVEL = 16;

    private final Map<String, ModelSession> modelSessionIdMap;
    private final Map<Object, ModelSession> modelRootCache;
    private volatile boolean closing = false;

    protected DefaultModelSessionManager(Map<String, ModelSession> modelSessionIdMap) {
        this.modelSessionIdMap = modelSessionIdMap;
        this.modelRootCache = Collections.synchronizedMap(new IdentityHashMap<Object, ModelSession>());
    }

    public static ModelSessionManager create() {
        return create(DEFAULT_CONCURRENCY_LEVEL);
    }

    public static ModelSessionManager create(int concurrencyLevel) {
        return new DefaultModelSessionManager(new ConcurrentHashMap<String, ModelSession>(INITIAL_CAPACITY,
                                                                                          LOAD_FACTOR,
                                                                                          concurrencyLevel));
    }

    @Override
    public ModelSession findByModelRoot(Object modelRoot) {

        ModelSession modelSession = modelRootCache.get(modelRoot);
        if (modelSession != null) {
            return modelSession;
        }

        for (Map.Entry<String, ModelSession> entry : modelSessionIdMap.entrySet()) {
            modelSession = entry.getValue();
            for (Object mr : modelSession.getModels().values()) {
                if (mr == modelRoot) {
                    modelRootCache.put(modelRoot, modelSession);
                    return modelSession;
                }
            }
        }

        return null;
    }

    @Override
    public ModelSession getById(String modelSessionId) {
        if (modelSessionId == null) {
            throw new NullPointerException("modelSessionId may not be null");
        }
        return modelSessionIdMap.get(modelSessionId);
    }

    @Override
    public void add(ModelSession modelSession) {
        checkClosing();
        modelSessionIdMap.put(modelSession.getId(), modelSession);
        LOG.debug("New ModelSession {} added", modelSession);
    }

    @Override
    public void remove(ModelSession modelSession) {
        checkClosing();
        modelSessionIdMap.remove(modelSession.getId());

        Iterator<ModelSession> it = modelRootCache.values().iterator();
        while (it.hasNext()) {
            ModelSession ms = it.next();
            if (ms == modelSession) {
                it.remove();
            }
        }

        LOG.debug("ModelSession {} removed", modelSession);
    }

    @Override
    public void close() {
        closing = true;
        for (ModelSession modelSession : modelSessionIdMap.values()) {
            modelSession.close();
        }
        modelSessionIdMap.clear();
        modelRootCache.clear();
    }

    private void checkClosing() {
        if (closing) {
            throw new IllegalStateException("ModelSessionManager is closed and cannot be modified");
        }
    }

}
