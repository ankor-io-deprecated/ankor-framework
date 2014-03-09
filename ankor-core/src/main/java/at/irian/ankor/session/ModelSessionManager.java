package at.irian.ankor.session;

/**
 * The ModelSessionManager handles all ModelSessions that are currently active.
 * "Active" means that an application instance is currently associated with this model session.
 *
 * @author Manfred Geiler
 */
public interface ModelSessionManager {

    void add(ModelSession modelSession);

    /**
     *
     * @param modelRoot
     * @return
     */
    ModelSession findByModelRoot(Object modelRoot);

    /**
     * @param modelSessionId  unique id of session to find
     * @return ModelSession with the given id or null if not found (or no longer exists)
     */
    ModelSession getById(String modelSessionId);  //todo  throw Exception if not found?

     /**
     * Invalidate (i.e. close) the given ModelSession.
     * ModelSessionManager implementations must call the {@link ModelSession#close()} method
     * on the given instance and remove any reference to the ModelSession.
     * @param modelSession  ModelSession to remove
     */
    void remove(ModelSession modelSession);

}
