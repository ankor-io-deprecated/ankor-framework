package at.irian.ankor.core.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
public class BeanELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELContext.class);

    private final ELContext baseELContext;
    private final VariableMapper variableMapper;

    public BeanELContext(ExpressionFactory expressionFactory,
                         ELContext baseELContext,
                         BeanResolver beanResolver) {
        this.baseELContext = baseELContext;
        this.variableMapper = new BeanVariableMapper(expressionFactory, baseELContext, beanResolver);
    }

    @Override
    public ELResolver getELResolver() {
        return baseELContext.getELResolver();
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return baseELContext.getFunctionMapper();
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }

}
