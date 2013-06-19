package at.irian.ankor.core.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ELSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELSupport.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext standardELContext;

    public ELSupport(ExpressionFactory expressionFactory, ELContext standardELContext) {
        this.expressionFactory = expressionFactory;
        this.standardELContext = standardELContext;
    }

    public ExpressionFactory getExpressionFactory() {
        return expressionFactory;
    }

    public ELContext getStandardELContext() {
        return standardELContext;
    }
}
