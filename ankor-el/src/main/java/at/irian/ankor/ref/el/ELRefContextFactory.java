package at.irian.ankor.ref.el;

import at.irian.ankor.base.BeanResolver;
import at.irian.ankor.context.ModelContext;
import at.irian.ankor.el.AnkorELSupport;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefContextFactory;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import com.typesafe.config.Config;

import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ELRefContextFactory implements RefContextFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContextFactory.class);

    private final Config config;
    private final BeanResolver beanResolver;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;

    public ELRefContextFactory(Config config,
                               BeanResolver beanResolver,
                               List<ViewModelPostProcessor> viewModelPostProcessors) {
        this.config = config;
        this.beanResolver = beanResolver;
        this.viewModelPostProcessors = viewModelPostProcessors;
    }

    @Override
    public RefContext createRefContextFor(ModelContext modelContext) {
        ELSupport elSupport = new AnkorELSupport(config, modelContext, beanResolver);
        return new ELRefContext(elSupport,
                                config,
                                modelContext,
                                viewModelPostProcessors
        );
    }

}
