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
     * Find the ModelSession that currently "holds" the given model root.
     * @param modelRoot  model root to search for
     * @return a ModelSession that the given model root is currently associated with,
     *         or null if there is no such ModelSession
     */
    ModelSession findByModelRoot(Object modelRoot);

    /**
     * @param modelSessionId  unique id of session to find
     * @return ModelSession with the given id or null if not found (or no longer exists)
     */
    ModelSession getById(String modelSessionId);  //todo  throw Exception if not found?

    /**
     * Remove the given ModelSession from this ModelSessionManager.
     * Note: this method does NOT close the given ModelSession, this must be done by the caller.
     * @param modelSession  ModelSession to remove
     */
    void remove(ModelSession modelSession);

    /**
     * Close all ModelSessions and free all resources.
     */
    void close();
}
