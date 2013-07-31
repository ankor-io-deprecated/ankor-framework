package at.irian.ankor.context;

/**
 * @author Manfred Geiler
 */
public interface ModelContextManager {

    ModelContext getOrCreate(String modelContextId);

    void invalidate(ModelContext modelContext);

}
