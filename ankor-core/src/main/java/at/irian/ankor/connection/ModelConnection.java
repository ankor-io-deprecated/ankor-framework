package at.irian.ankor.connection;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * A ModelConnection is the abstraction of a single connected remote system to one specific ModelContext.
 * Typical client implementations have exactly one ModelConnection instance.
 * Typical server implementations have one ModelConnection for every connected client per ModelContext.
 * The relation between ModelConnection and ModelContext normally is 1:1.
 * In more complex environments (multiple collaborating clients) there could also be multiple ModelConnections
 * related to one ModelContext.
 *
 * @author Manfred Geiler
 */
public interface ModelConnection {

    void init();

    void close();

    ModelContext getModelContext();

    RefContext getRefContext();

    MessageSender getMessageSender();
}
