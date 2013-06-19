package at.irian.ankor.core.el;

import javax.el.*;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SingleReadonlyVariableELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingleReadonlyVariableELContext.class);

    private final CompositeELResolver elResolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public SingleReadonlyVariableELContext(ELContext baseELContext,
                                           String varName,
                                           Object value) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new SingleReadonlyVariableELResolver(varName, value));
        this.elResolver.add(baseELContext.getELResolver());
        this.functionMapper = baseELContext.getFunctionMapper();
        this.variableMapper = baseELContext.getVariableMapper();
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

}
