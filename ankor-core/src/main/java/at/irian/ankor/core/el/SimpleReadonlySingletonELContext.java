package at.irian.ankor.core.el;

import javax.el.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleReadonlySingletonELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleReadonlySingletonELContext.class);

    private final ELContext baseELContext;
    private final VariableMapper variableMapper;

    public SimpleReadonlySingletonELContext(ELContext baseELContext,
                                            String singletonName,
                                            ValueExpression singletonValueExpression) {
        this.baseELContext = baseELContext;
        this.variableMapper = new SimpleReadonlySingletonVariableMapper(baseELContext.getVariableMapper(),
                                                                        singletonName,
                                                                        singletonValueExpression);
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
