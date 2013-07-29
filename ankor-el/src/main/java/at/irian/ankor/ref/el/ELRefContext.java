package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.dispatch.EventDispatcher;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventDelaySupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.model.ViewModelPostProcessor;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.session.Session;
import com.typesafe.config.Config;

import javax.el.ExpressionFactory;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ELRefContext implements RefContext, RefContextImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final StandardELContext elContext;
    private final String modelRootVarName;
    private final ModelContext modelContext;
    private final EventDelaySupport eventDelaySupport;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;

    private Session session;


    ELRefContext(ELSupport elSupport,
                 Config config,
                 ModelContext modelContext,
                 EventDelaySupport eventDelaySupport,
                 List<ViewModelPostProcessor> viewModelPostProcessors) {
        this.viewModelPostProcessors = viewModelPostProcessors;
        this.expressionFactory = elSupport.getExpressionFactory();
        this.elContext = elSupport.getELContextFor(refFactory());
        this.modelContext = modelContext;
        this.eventDelaySupport = eventDelaySupport;
        this.modelRootVarName = config.getString("ankor.variable-names.modelRoot");
    }

    @Override
    public RefFactory refFactory() {
        return new ELRefFactory(this);
    }

    ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public StandardELContext getElContext() {
        return elContext;
    }

    @Override
    public EventListeners eventListeners() {
        return modelContext.getEventListeners();
    }

    String getModelRootVarName() {
        return modelRootVarName;
    }

    @Override
    public PathSyntax pathSyntax() {
        return ELPathSyntax.getInstance();
    }

    public EventDelaySupport eventDelaySupport() {
        return eventDelaySupport;
    }

    @Override
    public List<ViewModelPostProcessor> viewModelPostProcessors() {
        return viewModelPostProcessors;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public EventDispatcher eventDispatcher() {
        return session.getEventDispatcher();
    }

    @Override
    public Session session() {
        return session;
    }
}
