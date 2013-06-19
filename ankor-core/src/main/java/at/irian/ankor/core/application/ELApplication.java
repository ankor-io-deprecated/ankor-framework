package at.irian.ankor.core.application;

import at.irian.ankor.core.el.ModelELContext;
import at.irian.ankor.core.ref.RefFactory;
import at.irian.ankor.core.ref.el.ELRefContext;
import at.irian.ankor.core.ref.el.ELRefFactory;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author Manfred Geiler
 */
public class ELApplication extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELApplication.class);

    private final ExpressionFactory expressionFactory;
    protected final RefFactory refFactory;

    public ELApplication(Class<?> modelType,
                         Config config,
                         ExpressionFactory expressionFactory,
                         ELContext baseELContext) {
        super(modelType, config);
        this.expressionFactory = expressionFactory;
        ELContext modelELContext = new ModelELContext(baseELContext,
                                                      getModelHolder(),
                                                      getConfig().getModelRootVarName(),
                                                      getConfig().getModelHolderVarName());
        ELRefContext refContext = new ELRefContext(expressionFactory,
                                                   modelELContext,
                                                   new DefaultChangeNotifier(getListenerRegistry()),
                                                   new DefaultActionNotifier(getListenerRegistry()),
                                                   config.getModelRootVarName());
        this.refFactory = new ELRefFactory(refContext);
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public RefFactory getRefFactory() {
        return refFactory;
    }
}
