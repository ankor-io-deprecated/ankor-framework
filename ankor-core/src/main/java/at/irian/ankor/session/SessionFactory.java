package at.irian.ankor.session;

/**
 * @author Manfred Geiler
 */
public interface SessionFactory {

    Session create(String sessionId);

}
