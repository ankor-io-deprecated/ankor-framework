package at.irian.ankor.core.method;

import at.irian.ankor.core.application.ModelHolder;
import at.irian.ankor.core.el.BeanELContext;
import at.irian.ankor.core.el.BeanResolver;
import at.irian.ankor.core.el.ModelContextELContext;
import at.irian.ankor.core.el.ModelHolderELContext;
import at.irian.ankor.core.ref.ModelRef;
import at.irian.ankor.core.ref.PathUtils;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.MethodExpression;
import javax.el.ValueExpression;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MethodExecutor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MethodExecutor.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext modelAndBeanELContext;

    public MethodExecutor(ExpressionFactory expressionFactory,
                          ELContext standardElContext,
                          ModelHolder modelHolder,
                          BeanResolver beanResolver) {
        this.expressionFactory = expressionFactory;
        BeanELContext beanELContext = new BeanELContext(expressionFactory, standardElContext, beanResolver);
        this.modelAndBeanELContext = new ModelHolderELContext(expressionFactory, beanELContext, modelHolder);
    }

    public Object execute(String methodExpression, ModelRef actionContext) {
        ValueExpression ve = expressionFactory.createValueExpression(modelAndBeanELContext,
                                                                     PathUtils.pathToValueExpression(actionContext.path()),
                                                                     Object.class);
        ModelContextELContext elContext = new ModelContextELContext(modelAndBeanELContext, ve);

        ValueExpression me = expressionFactory.createValueExpression(elContext, "#{" + methodExpression + "}", Object.class);

        return me.getValue(elContext);
    }

}
