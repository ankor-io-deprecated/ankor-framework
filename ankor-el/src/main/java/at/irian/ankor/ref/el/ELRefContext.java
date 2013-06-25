package at.irian.ankor.ref.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.el.ModelContextELResolver;
import at.irian.ankor.el.ModelHolderELResolver;
import at.irian.ankor.el.ModelRootELResolver;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.EventBus;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.RefFactory;
import com.typesafe.config.Config;

import javax.el.ELResolver;
import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefContext implements RefContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final StandardELContext elContext;
    private final Config config;
    private final String modelRootVarName;
    private final String modelContextPath;
    private final EventBus eventBus;
    private final ELRefFactory refFactory;

    private ELRefContext(ExpressionFactory expressionFactory,
                         StandardELContext elContext,
                         Config config,
                         EventBus eventBus,
                         String modelContextPath) {
        this.expressionFactory = expressionFactory;
        this.elContext = elContext;
        this.config = config;
        this.modelRootVarName = config.getString("ankor.variable-names.modelRoot");
        this.modelContextPath = modelContextPath;
        this.eventBus = eventBus;
        this.refFactory = new ELRefFactory(this);
    }

    public static ELRefContext create(ExpressionFactory expressionFactory,
                                      StandardELContext baseELContext,
                                      Config config,
                                      ModelHolder modelHolder) {
        StandardELContext elContext = baseELContext.withAdditional(new ModelRootELResolver(config, modelHolder))
                                                   .withAdditional(new ModelHolderELResolver(config, modelHolder));
        return new ELRefContext(expressionFactory, elContext, config, modelHolder.getEventBus(), null);
    }

    @Override
    public RefFactory getRefFactory() {
        return refFactory;
    }

    ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public StandardELContext getElContext() {
        return elContext;
    }

    EventBus getEventBus() {
        return eventBus;
    }

    String getModelRootVarName() {
        return modelRootVarName;
    }

    @Override
    public PathSyntax getPathSyntax() {
        return ELPathSyntax.getInstance();
    }

    @Override
    public String getModelContextPath() {
        return modelContextPath;
    }

    public ELRefContext withModelContextPath(String modelContextPath) {
        return withAdditionalELResolver(new ModelContextELResolver(expressionFactory,
                                                                   config,
                                                                   modelContextPath,
                                                                   refFactory));
    }

    public ELRefContext withAdditionalELResolver(ELResolver elResolver) {
        return new ELRefContext(expressionFactory,
                                elContext.withAdditional(elResolver),
                                config, eventBus, modelContextPath);
    }

}
