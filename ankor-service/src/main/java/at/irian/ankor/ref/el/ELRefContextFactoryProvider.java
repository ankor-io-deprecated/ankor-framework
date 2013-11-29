package at.irian.ankor.ref.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.ref.RefContextFactoryProvider;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;

import java.util.List;

/**
* @author Manfred Geiler
*/
public class ELRefContextFactoryProvider extends RefContextFactoryProvider {
    @Override
    public RefContextFactory createRefContextFactory(BeanResolver beanResolver,
                                                     List<ViewModelPostProcessor> viewModelPostProcessors,
                                                     Scheduler scheduler,
                                                     ModelRootFactory modelRootFactory) {
        return new ELRefContextFactory(beanResolver, viewModelPostProcessors, scheduler, modelRootFactory);
    }
}
