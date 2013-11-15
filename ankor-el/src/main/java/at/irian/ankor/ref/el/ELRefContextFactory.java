package at.irian.ankor.ref.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.el.AnkorELSupport;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.session.ModelRootFactory;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ELRefContextFactory implements RefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContextFactory.class);

    private final BeanResolver beanResolver;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;
    private final Scheduler scheduler;
    private final ModelRootFactory modelRootFactory;

    public ELRefContextFactory(BeanResolver beanResolver,
                               List<ViewModelPostProcessor> viewModelPostProcessors,
                               Scheduler scheduler,
                               ModelRootFactory modelRootFactory) {
        this.beanResolver = beanResolver;
        this.viewModelPostProcessors = viewModelPostProcessors;
        this.scheduler = scheduler;
        this.modelRootFactory = modelRootFactory;
    }

    @Override
    public RefContext createRefContextFor(ModelContext modelContext) {
        ELSupport elSupport = new AnkorELSupport(modelContext, beanResolver, modelRootFactory);
        return ELRefContext.create(elSupport,
                                   modelContext,
                                   viewModelPostProcessors,
                                   scheduler);
    }

}
