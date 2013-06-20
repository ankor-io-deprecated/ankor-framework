package at.irian.ankor.el;

import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.FunctionMapper;
import javax.el.VariableMapper;

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
