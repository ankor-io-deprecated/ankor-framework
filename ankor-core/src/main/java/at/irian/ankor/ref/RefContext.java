package at.irian.ankor.ref;

import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

import java.util.List;
import java.util.Map;

/**
 * todo document
 * todo rename to ModelContext
 *
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

    void openModelConnection(String modelName, Map<String, Object> connectParameters);

    void closeModelConnection(String modelName);
}
