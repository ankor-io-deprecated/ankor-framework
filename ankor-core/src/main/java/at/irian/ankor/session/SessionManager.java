package at.irian.ankor.session;

/**
 * @author Manfred Geiler
 */
public interface SessionManager {

    Session getOrCreateSession(String id);

    void invalidateSession(String id);

}
