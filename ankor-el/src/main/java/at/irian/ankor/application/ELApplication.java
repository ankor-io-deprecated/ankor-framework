package at.irian.ankor.application;

import at.irian.ankor.el.ModelELContext;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.ref.el.ELRefContext;
import at.irian.ankor.ref.el.ELRefFactory;
import com.typesafe.config.Config;

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
        ELContext modelELContext = new ModelELContext(baseELContext, getModelHolder(), config);
        ELRefContext refContext = ELRefContext.create(expressionFactory,
                                                      modelELContext,
                                                      new DefaultChangeNotifier(getListenerRegistry()),
                                                      new DefaultActionNotifier(getListenerRegistry()),
                                                      config, getListenerRegistry());
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
