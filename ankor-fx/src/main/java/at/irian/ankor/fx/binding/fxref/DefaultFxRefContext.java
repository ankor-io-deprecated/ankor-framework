package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContext;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

import java.util.List;

/**
 * @author Manfred Geiler
 */
class DefaultFxRefContext extends ELRefContext implements FxRefContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultFxRefContext.class);

    protected DefaultFxRefContext(ELSupport elSupport,
                                  ModelContext modelContext,
                                  List<ViewModelPostProcessor> viewModelPostProcessors,
                                  Scheduler scheduler,
                                  RefFactory refFactory,
                                  BeanMetadataProvider metadataProvider,
                                  BeanFactory beanFactory) {
        super(elSupport, modelContext, viewModelPostProcessors, scheduler, refFactory, metadataProvider, beanFactory);
    }

    protected static DefaultFxRefContext create(ELSupport elSupport,
                                                ModelContext modelContext,
                                                List<ViewModelPostProcessor> viewModelPostProcessors,
                                                Scheduler scheduler,
                                                BeanMetadataProvider metadataProvider,
                                                BeanFactory beanFactory) {
        DefaultFxRefFactory refFactory = new DefaultFxRefFactory();
        DefaultFxRefContext refContext = new DefaultFxRefContext(elSupport,
                                                   modelContext,
                                                   viewModelPostProcessors,
                                                   scheduler,
                                                   refFactory, metadataProvider, beanFactory);
        refFactory.setRefContext(refContext); // bi-directional relation - not nice but no idea by now how to make it nice...  ;-)
        return refContext;
    }

    @Override
    public FxRefFactory refFactory() {
        return (FxRefFactory) super.refFactory();
    }
}
