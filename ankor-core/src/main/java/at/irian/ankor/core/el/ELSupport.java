package at.irian.ankor.core.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import static at.irian.ankor.core.ref.el.PathUtils.pathToValueExpression;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELSupport.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext modelAndBeanELContext;

    public ELSupport(ExpressionFactory expressionFactory, ELContext baseELContext) {
        this.expressionFactory = expressionFactory;
        this.modelAndBeanELContext = baseELContext;
    }

    public ELContext getBaseELContext() {
        return modelAndBeanELContext;
    }

    public ValueExpression createRootValueExpression(ELContext elContext) {
        return expressionFactory.createValueExpression(elContext,
                                                       pathToValueExpression("modelHolder.model"), //todo
                                                       Object.class);
    }

    public ValueExpression createValueExpression(ELContext elContext, String path) {
        return expressionFactory.createValueExpression(elContext,
                                                       pathToValueExpression(path),
                                                       Object.class);
    }

    public Object executeMethod(ELContext elContext, String methodExpression) {
        ValueExpression ve = expressionFactory.createValueExpression(elContext,
                                                                     "#{" + methodExpression + "}",
                                                                     Object.class);
        return ve.getValue(elContext);
    }
}
