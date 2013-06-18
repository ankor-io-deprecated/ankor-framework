package at.irian.ankor.core.el;

import javax.el.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ModelContextELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelContextELContext.class);

    private final ELContext baseELContext;
    private final VariableMapper variableMapper;

    public ModelContextELContext(ELContext baseELContext, ValueExpression contextValueExpression) {
        this.baseELContext = baseELContext;
        this.variableMapper = new ModelContextVariableMapper(baseELContext, contextValueExpression);
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
