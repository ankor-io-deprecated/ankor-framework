package at.irian.ankor.core.el;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
public class BeanResolverELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELContext.class);

    private final CompositeELResolver elResolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public BeanResolverELContext(ELContext baseELContext, BeanResolver beanResolver) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new BeanResolverELResolver(beanResolver));
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
