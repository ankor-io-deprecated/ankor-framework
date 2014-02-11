package at.irian.ankor.context;

/**
 * The ModelContextManager handles all ModelContexts that are currently active.
 * "Active" means that an application (i.e. user session) is currently connected to this model.
 *
 * @author Manfred Geiler
 */
public interface ModelContextManager {

    /**
     * Get the ModelContext with the given id. Create a new ModelContext (by means of a {@link ModelContextFactory})
     * if there is none with this id yet.
     * @param modelContextId  model id
     * @return the existing (or a newly created) ModelContext with the given id
     */
    ModelContext getOrCreate(String modelContextId);

    /**
     * Invalidate (i.e. close) the given ModelContext.
     * ModelContextManager implementations must call the {@link at.irian.ankor.context.ModelContext#close()} method
     * on the given instance and remove any reference to the ModelContext.
     * @param modelContext  ModelContext to invalidate
     */
    void invalidate(ModelContext modelContext);

}
