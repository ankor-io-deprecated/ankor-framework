package at.irian.ankor.session;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.ref.RefContext;

/**
 * A Session is the abstraction of a single connected remote system.
 * Typical client implementations have exactly one Session instance.
 * Typical server implementations have one Session for every connected client.
 * The relation between Session and ModelContext normally is 1:1.
 * In more complex environments (multiple collaborating clients) there could also be multiple Sessions related to
 * one ModelContext.
 *
 * @author Manfred Geiler
 */
public interface Session {

    void init();

    void close();

    ModelContext getModelContext();

    RefContext getRefContext();

    MessageSender getMessageSender();
}
