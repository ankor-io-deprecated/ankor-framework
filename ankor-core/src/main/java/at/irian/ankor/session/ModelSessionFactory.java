package at.irian.ankor.session;

import at.irian.ankor.application.ApplicationInstance;

/**
 * Factory for ModelSession.
 *
 * @author Manfred Geiler
 */
public interface ModelSessionFactory {
    /**
     * Create a new ModelSession for the given application instance.
     * @param applicationInstance  application instance
     * @return the newly created ModelSession
     */
    ModelSession createModelSession(ApplicationInstance applicationInstance);
}
