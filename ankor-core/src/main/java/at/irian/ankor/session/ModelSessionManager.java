package at.irian.ankor.session;

import at.irian.ankor.application.ApplicationInstance;

/**
 * The ModelSessionManager handles all ModelSessions that are currently active.
 * "Active" means that an application instance is currently associated with this model session.
 *
 * @author Manfred Geiler
 */
public interface ModelSessionManager {

    /**
     * Get the ModelSession for the given application instance.
     * Create a new ModelSession (by means of a {@link ModelSessionFactory})
     * if there is none associated with this application instance yet.
     * @param applicationInstance  application instance
     * @return the existing (or a newly created) ModelSession associated with the given application instance
     */
    ModelSession getOrCreate(ApplicationInstance applicationInstance);

    /**
     * @param modelSessionId  unique id of session to find
     * @return ModelSession with the given id or null if not found (or no longer exists)
     */
    ModelSession getById(String modelSessionId);

     /**
     * Invalidate (i.e. close) the given ModelSession.
     * ModelSessionManager implementations must call the {@link ModelSession#close()} method
     * on the given instance and remove any reference to the ModelSession.
     * @param modelSession  ModelSession to invalidate
     */
    void invalidate(ModelSession modelSession);
}
