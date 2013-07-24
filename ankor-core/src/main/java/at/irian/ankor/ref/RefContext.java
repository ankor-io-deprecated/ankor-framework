package at.irian.ankor.ref;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.path.PathSyntax;

/**
 * @author Manfred Geiler
 */
public interface RefContext {

    PathSyntax pathSyntax();

    RefFactory refFactory();

    EventListeners eventListeners();

    MessageSender messageSender();

    RefContext withMessageSender(MessageSender newMessageSender);
}
