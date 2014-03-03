package at.irian.ankor.ref.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.el.AnkorELSupport;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.session.ModelSession;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ELRefContextFactory implements RefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContextFactory.class);

    private final BeanResolver beanResolver;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;
    private final Scheduler scheduler;
    private final BeanMetadataProvider metadataProvider;
    private BeanFactory beanFactory;

    public ELRefContextFactory(BeanResolver beanResolver,
                               List<ViewModelPostProcessor> viewModelPostProcessors,
                               Scheduler scheduler,
                               BeanMetadataProvider metadataProvider,
                               BeanFactory beanFactory) {
        this.beanResolver = beanResolver;
        this.viewModelPostProcessors = viewModelPostProcessors;
        this.scheduler = scheduler;
        this.metadataProvider = metadataProvider;
        this.beanFactory = beanFactory;
    }

    @Override
    public RefContext createRefContextFor(ModelSession modelSession) {
        ELSupport elSupport = new AnkorELSupport(modelSession, beanResolver);
        return ELRefContext.create(elSupport,
                                   modelSession,
                                   viewModelPostProcessors,
                                   scheduler,
                                   metadataProvider,
                                   beanFactory);
    }

}
