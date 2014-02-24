package at.irian.ankor.connection;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * A ModelConnection is the abstraction of a single connected remote system to one specific ModelSession.
 * Typical client implementations have exactly one ModelConnection instance.
 * Typical server implementations have one ModelConnection for every connected client per ModelSession.
 * The relation between ModelConnection and ModelSession normally is 1:1.
 * In more complex environments (multiple collaborating clients) there could also be multiple ModelConnections
 * related to one ModelSession.
 *
 * @author Manfred Geiler
 */
public interface ModelConnection {

    void init();

    void close();

    ModelSession getModelSession();

    RefContext getRefContext();

    MessageSender getMessageSender();
}
