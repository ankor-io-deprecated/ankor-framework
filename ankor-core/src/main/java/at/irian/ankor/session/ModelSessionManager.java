package at.irian.ankor.session;

/**
 * The ModelSessionManager handles all ModelSessions that are currently active.
 * "Active" means that an application is currently connected to this model.
 *
 * @author Manfred Geiler
 */
public interface ModelSessionManager {

    /**
     * Get the ModelSession with the given id. Create a new ModelSession (by means of a {@link ModelSessionFactory})
     * if there is none with this id yet.
     * @param modelSessionId  model id
     * @return the existing (or a newly created) ModelSession with the given id
     */
    ModelSession getOrCreate(String modelSessionId);

    /**
     * Invalidate (i.e. close) the given ModelSession.
     * ModelSessionManager implementations must call the {@link ModelSession#close()} method
     * on the given instance and remove any reference to the ModelSession.
     * @param modelSession  ModelSession to invalidate
     */
    void invalidate(ModelSession modelSession);

}
