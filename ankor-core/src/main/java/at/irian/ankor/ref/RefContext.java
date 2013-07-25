package at.irian.ankor.ref;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.messaging.MessageSender;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.path.PathSyntax;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public interface RefContext {

    PathSyntax pathSyntax();

    RefFactory refFactory();

    EventListeners eventListeners();

    MessageSender messageSender();

    RefContext withMessageSender(MessageSender newMessageSender);

    List<ViewModelPostProcessor> viewModelPostProcessors();
}
