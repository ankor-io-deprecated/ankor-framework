package at.irian.ankor.context;

/**
 * Factory for ModelContext.
 *
 * @author Manfred Geiler
 */
public interface ModelContextFactory {
    /**
     * Create a new ModelContext with the given unique id.
     * @param modelContextId  unique model id
     * @return the newly created ModelContext
     */
    ModelContext createModelContext(String modelContextId);
}
