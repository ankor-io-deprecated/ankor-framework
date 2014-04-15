package at.irian.ankor.ref.el;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.delay.Scheduler;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.switching.Switchboard;
import at.irian.ankor.switching.connector.local.StatefulSessionModelAddress;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.factory.BeanFactory;
import at.irian.ankor.viewmodel.metadata.BeanMetadataProvider;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import java.util.List;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class ELRefContext implements RefContext, RefContextImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ELSupport elSupport;
    private final ModelSession modelSession;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;
    private final Scheduler scheduler;
    private final RefFactory refFactory;
    private final BeanMetadataProvider metadataProvider;
    private final BeanFactory beanFactory;
    private final Switchboard switchboard;

    protected ELRefContext(ELSupport elSupport,
                           ModelSession modelSession,
                           List<ViewModelPostProcessor> viewModelPostProcessors,
                           Scheduler scheduler,
                           RefFactory refFactory,
                           BeanMetadataProvider metadataProvider,
                           BeanFactory beanFactory,
                           Switchboard switchboard) {
        this.elSupport = elSupport;
        this.modelSession = modelSession;
        this.viewModelPostProcessors = viewModelPostProcessors;
        this.scheduler = scheduler;
        this.refFactory = refFactory;
        this.metadataProvider = metadataProvider;
        this.beanFactory = beanFactory;
        this.switchboard = switchboard;
    }

    protected static ELRefContext create(ELSupport elSupport,
                                         ModelSession modelSession,
                                         List<ViewModelPostProcessor> viewModelPostProcessors,
                                         Scheduler scheduler,
                                         BeanMetadataProvider metadataProvider,
                                         BeanFactory beanFactory,
                                         Switchboard switchboard) {
        ELRefFactory refFactory = new ELRefFactory();
        ELRefContext refContext = new ELRefContext(elSupport,
                                                   modelSession,
                                                   viewModelPostProcessors,
                                                   scheduler,
                                                   refFactory,
                                                   metadataProvider,
                                                   beanFactory,
                                                   switchboard);
        refFactory.setRefContext(refContext); // bi-directional relation - not nice but no idea by now how to make it nice...  ;-)
        return refContext;
    }

    @Override
    public RefFactory refFactory() {
        return refFactory;
    }

    ExpressionFactory getExpressionFactory() {
        return elSupport.getExpressionFactory();
    }

    ELContext createELContext() {
        return elSupport.getELContextFor(refFactory());
    }

    @Override
    public EventListeners eventListeners() {
        return modelSession.getEventListeners();
    }

    @Override
    public PathSyntax pathSyntax() {
        return SimpleELPathSyntax.getInstance();
    }

    @Override
    public List<ViewModelPostProcessor> viewModelPostProcessors() {
        return viewModelPostProcessors;
    }

    @Override
    public ModelSession modelSession() {
        return modelSession;
    }

    @Override
    public Scheduler scheduler() {
        return scheduler;
    }

    @Override
    public BeanMetadataProvider metadataProvider() {
        return metadataProvider;
    }

    @Override
    public BeanFactory beanFactory() {
        return beanFactory;
    }

    @Override
    public void openModelConnection(String modelName, Map<String, Object> connectParameters) {
        switchboard.openConnection(new StatefulSessionModelAddress(modelSession, modelName), connectParameters);
    }

    @Override
    public void closeModelConnection(String modelName) {
        switchboard.closeAllConnections(new StatefulSessionModelAddress(modelSession, modelName));
    }
}
