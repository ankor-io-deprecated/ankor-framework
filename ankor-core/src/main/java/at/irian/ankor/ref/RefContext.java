package at.irian.ankor.ref;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.session.Session;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public interface RefContext {

    PathSyntax pathSyntax();

    RefFactory refFactory();

    EventListeners eventListeners();

    List<ViewModelPostProcessor> viewModelPostProcessors();

    Session session();

}
