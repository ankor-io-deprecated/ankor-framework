package at.irian.ankor.ref.el;

import at.irian.ankor.application.ListenerRegistry;
import at.irian.ankor.application.ModelHolder;
import at.irian.ankor.el.ModelContextELContext;
import at.irian.ankor.el.ModelELContext;
import at.irian.ankor.el.SingleReadonlyVariableELContext;
import at.irian.ankor.el.StandardELContext;
import at.irian.ankor.event.ActionNotifier;
import at.irian.ankor.event.ChangeNotifier;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.path.el.ELPathSyntax;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import com.typesafe.config.Config;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELRefContext implements RefContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefContext.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext elContext;
    private final ChangeNotifier changeNotifier;
    private final ActionNotifier actionNotifier;
    private final Config config;
    private final String modelRootVarName;
    private final Ref modelContext;
    private final ListenerRegistry listenerRegistry;

    private ELRefContext(ExpressionFactory expressionFactory,
                         ELContext elContext,
                         ChangeNotifier changeNotifier,
                         ActionNotifier actionNotifier,
                         Config config,
                         String modelRootVarName,
                         Ref modelContext,
                         ListenerRegistry listenerRegistry) {
        this.expressionFactory = expressionFactory;
        this.elContext = elContext;
        this.changeNotifier = changeNotifier;
        this.actionNotifier = actionNotifier;
        this.config = config;
        this.modelRootVarName = modelRootVarName;
        this.modelContext = modelContext;
        this.listenerRegistry = listenerRegistry;
    }

    public static ELRefContext create(ExpressionFactory expressionFactory,
                                      ELContext elContext,
                                      ChangeNotifier changeNotifier,
                                      ActionNotifier actionNotifier,
                                      Config config, ListenerRegistry listenerRegistry) {
        ELRefContext refContext = new ELRefContext(expressionFactory,
                                                   elContext,
                                                   changeNotifier,
                                                   actionNotifier,
                                                   config,
                                                   config.getString("ankor.variable-names.modelRoot"),
                                                   null,
                                                   listenerRegistry);
        ELContext rootRefELContext = new SingleReadonlyVariableELContext(elContext,
                                                             config.getString("ankor.variable-names.modelRootRef"),
                                                             ELRefUtils.rootRef(refContext, false));
        return refContext.with(rootRefELContext);
    }

    public static ELRefContext create(ModelHolder modelHolder,
                                      ChangeNotifier changeNotifier,
                                      ActionNotifier actionNotifier,
                                      Config config, ListenerRegistry listenerRegistry) {
        ExpressionFactory expressionFactory = ExpressionFactory.newInstance();
        ModelELContext modelELContext = new ModelELContext(new StandardELContext(), modelHolder, config);
        return create(expressionFactory, modelELContext, changeNotifier, actionNotifier, config, listenerRegistry);
    }

    public ELRefContext with(ELContext elContext) {
        return new ELRefContext(expressionFactory, elContext, changeNotifier, actionNotifier, config,
                                modelRootVarName,
                                modelContext, listenerRegistry);
    }

    public ELRefContext withNoModelChangeNotifier() {
        return new ELRefContext(expressionFactory, elContext, null, actionNotifier, config,
                                modelRootVarName,
                                modelContext, listenerRegistry);
    }

    @Override
    public ELRefContext withModelContext(Ref modelContext) {
        ModelContextELContext elContext = new ModelContextELContext(this.elContext, config, modelContext);
        return new ELRefContext(expressionFactory,
                                elContext,
                                changeNotifier,
                                actionNotifier,
                                config,
                                modelRootVarName, modelContext, listenerRegistry);
    }

    public ELContext getELContext() {
        return elContext;
    }

    public ChangeNotifier getChangeNotifier() {
        return changeNotifier;
    }

    public ActionNotifier getActionNotifier() {
        return actionNotifier;
    }

    public String getRootPath() {
        return modelRootVarName;
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public Ref getModelContext() {
        return modelContext;
    }

    public ListenerRegistry getListenerRegistry() {
        return listenerRegistry;
    }

    @Override
    public PathSyntax getPathSyntax() {
        return ELPathSyntax.getInstance();
    }
}
