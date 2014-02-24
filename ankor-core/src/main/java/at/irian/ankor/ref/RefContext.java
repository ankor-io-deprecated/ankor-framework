package at.irian.ankor.ref;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public interface RefContext {

    PathSyntax pathSyntax();

    RefFactory refFactory();

    List<ViewModelPostProcessor> viewModelPostProcessors();

    ModelSession modelSession();

    Scheduler scheduler();

    BeanMetadataProvider metadataProvider();

    BeanFactory beanFactory();

}
