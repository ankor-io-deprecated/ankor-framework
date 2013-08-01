package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelContext;
import at.irian.ankor.el.ELSupport;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.SimpleELPathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import com.typesafe.config.Config;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import java.util.List;

/**
 * @author Manfred Geiler
 */
public class ELRefContext implements RefContext, RefContextImplementor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ELSupport elSupport;
    private final String modelRootVarName;
    private final ModelContext modelContext;
    private final List<ViewModelPostProcessor> viewModelPostProcessors;
    private final ELRefFactory refFactory;

    ELRefContext(ELSupport elSupport,
                 Config config,
                 ModelContext modelContext,
                 List<ViewModelPostProcessor> viewModelPostProcessors) {
        this.elSupport = elSupport;
        this.modelRootVarName = config.getString("ankor.variable-names.modelRoot");
        this.modelContext = modelContext;
        this.viewModelPostProcessors = viewModelPostProcessors;
        this.refFactory = new ELRefFactory(this);
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
        return modelContext.getEventListeners();
    }

    String getModelRootVarName() {
        return modelRootVarName;
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
    public ModelContext modelContext() {
        return modelContext;
    }
}
