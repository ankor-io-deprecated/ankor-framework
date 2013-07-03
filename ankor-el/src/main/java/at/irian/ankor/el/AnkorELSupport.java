package at.irian.ankor.el;

import at.irian.ankor.context.ModelHolder;
import at.irian.ankor.ref.RefFactory;
import at.irian.ankor.system.BeanResolver;
import com.typesafe.config.Config;

import javax.el.ExpressionFactory;

/**
 * @author Manfred Geiler
 */
public class AnkorELSupport implements ELSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorELSupport.class);

    private final ExpressionFactory expressionFactory;
    private final Config config;
    private final ModelHolder modelHolder;
    private final BeanResolver beanResolver;

    public AnkorELSupport(Config config, ModelHolder modelHolder, BeanResolver beanResolver) {
        this.beanResolver = beanResolver;
        this.expressionFactory = ExpressionFactory.newInstance();
        this.config = config;
        this.modelHolder = modelHolder;
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    @Override
    public StandardELContext getELContextFor(RefFactory refFactory) {
        StandardELContext elContext = new StandardELContext();
        if (beanResolver != null) {
            elContext = elContext.withAdditional(new BeanResolverELResolver(beanResolver));
        }
        if (modelHolder != null) {
            elContext = elContext.withAdditional(new ModelRootELResolver(config, modelHolder, refFactory));
        }
        return elContext;
    }
}
