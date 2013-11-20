package at.irian.ankor.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Manfred Geiler
 */
public class DefaultModelContextManager implements ModelContextManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultModelContextManager.class);

    private final Map<String, ModelContext> modelContextMap = new ConcurrentHashMap<String, ModelContext>();
    private final ModelContextFactory modelContextFactory;

    public DefaultModelContextManager(ModelContextFactory modelContextFactory) {
        this.modelContextFactory = modelContextFactory;
    }

    @Override
    public ModelContext getOrCreate(String modelContextId) {
	if (modelContextId == null) {
	    throw new IllegalArgumentException("modelContextId may not be null");
	}
        ModelContext modelContext = modelContextMap.get(modelContextId);
        if (modelContext == null) {
            synchronized (modelContextMap) {
                modelContext = modelContextMap.get(modelContextId);
                if (modelContext == null) {
                    modelContext = modelContextFactory.createModelContext(modelContextId);
                    modelContextMap.put(modelContextId, modelContext);
                }
            }
        }
        return modelContext;
    }

    @Override
    public void invalidate(ModelContext modelContext) {
        synchronized (modelContextMap) {
            modelContext = modelContextMap.remove(modelContext.getId());
            modelContext.close();
        }
    }
}
