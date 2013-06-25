package at.irian.ankor.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
public class StandardELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELContext.class);

    private final ELResolver elResolver;
    private final FunctionMapper functionMapper;
    private final VariableMapper variableMapper;

    public StandardELContext() {
        this.elResolver = new StandardELResolver();
        this.functionMapper = new StandardFunctionMapper();
        this.variableMapper = new StandardVariableMapper();
    }

    protected StandardELContext(ELResolver elResolver, FunctionMapper functionMapper, VariableMapper variableMapper) {
        this.elResolver = elResolver;
        this.functionMapper = functionMapper;
        this.variableMapper = variableMapper;
    }

    @Override
    public ELResolver getELResolver() {
        return elResolver;
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return functionMapper;
    }

    @Override
    public VariableMapper getVariableMapper() {
        return variableMapper;
    }

    public StandardELContext withAdditional(ELResolver additionalELResolver) {
        CompositeELResolver newCompELResolver = new CompositeELResolver();
        newCompELResolver.add(additionalELResolver);
        newCompELResolver.add(this.elResolver);
        return new StandardELContext(newCompELResolver, functionMapper, variableMapper);
    }

}
