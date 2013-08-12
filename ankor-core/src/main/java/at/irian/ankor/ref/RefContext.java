package at.irian.ankor.ref;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public interface RefContext {

    PathSyntax pathSyntax();

    RefFactory refFactory();

    List<ViewModelPostProcessor> viewModelPostProcessors();

    ModelContext modelContext();

    Scheduler scheduler();

}
