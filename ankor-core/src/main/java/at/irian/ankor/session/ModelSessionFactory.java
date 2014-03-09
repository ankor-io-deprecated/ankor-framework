package at.irian.ankor.session;

/**
 * Factory for ModelSession.
 *
 * @author Manfred Geiler
 */
public interface ModelSessionFactory {
    /**
     * Create a new ModelSession.
     *
     * @return the newly created ModelSession
     */
    ModelSession createModelSession();
}
