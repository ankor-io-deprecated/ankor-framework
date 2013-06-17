package at.irian.ankor.core.ref;

import at.irian.ankor.core.model.ModelHolder;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
class ModelHolderELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELContext.class);

    private final ELContext baseELContext;
    private final VariableMapper variableMapper;

    public ModelHolderELContext(ExpressionFactory expressionFactory,
                                ELContext baseELContext,
                                ModelHolder modelHolder) {
        this.baseELContext = baseELContext;
        this.variableMapper = new ModelHolderVariableMapper(expressionFactory, baseELContext, modelHolder);
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
