package at.irian.ankor.context;

/**
 * @author Manfred Geiler
 */
public class SingletonModelContextManager implements ModelContextManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonModelContextManager.class);

    private final ModelContext modelContext;
    private String modelContextId;

    public SingletonModelContextManager(String modelContextId, ModelContext modelContext) {
        this.modelContext = modelContext;
        this.modelContextId = modelContextId;
    }

    @Override
    public ModelContext getOrCreate(String modelContextId) {
        if (modelContextId != null) {
            if (this.modelContextId != null) {
                if (!this.modelContextId.equals(modelContextId)) {
                    throw new IllegalStateException("wrong modelContext id " + modelContextId + " - expected " + this.modelContextId);
                }
            } else {
                this.modelContextId = modelContextId;
            }
        }
        return modelContext;
    }

    @Override
    public void invalidate(ModelContext modelContext) {
        modelContext.close();
    }
}
