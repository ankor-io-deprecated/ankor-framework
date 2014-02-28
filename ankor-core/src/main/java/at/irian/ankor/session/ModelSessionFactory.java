package at.irian.ankor.session;

/**
 * Factory for ModelSession.
 *
 * @author Manfred Geiler
 */
public interface ModelSessionFactory {
    /**
     * Create a new ModelSession with the given unique id.
     * @param modelSessionId  unique model id
     * @return the newly created ModelSession
     */
    ModelSession createModelSession(String modelSessionId);
}
