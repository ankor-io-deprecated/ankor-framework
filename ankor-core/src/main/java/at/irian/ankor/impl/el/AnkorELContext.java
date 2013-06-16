package at.irian.ankor.impl.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
public class AnkorELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorELContext.class);

    private final ELResolver elResolver;
    private final FunctionMapper functionMapper;
    private final VariableMapper variableMapper;

    public AnkorELContext(ELResolver elResolver, ValueExpression model) {
        this.elResolver = elResolver;
        this.functionMapper = new AnkorFunctionMapper();
        this.variableMapper = new AnkorVariableMapper(model);
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
