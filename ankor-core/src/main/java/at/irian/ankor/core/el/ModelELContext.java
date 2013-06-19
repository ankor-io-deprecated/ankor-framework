package at.irian.ankor.core.el;

import at.irian.ankor.core.application.ModelHolder;

import javax.el.*;

/**
 * @author Manfred Geiler
 */
public class ModelELContext extends ELContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StandardELContext.class);

    private final CompositeELResolver elResolver;
    private FunctionMapper functionMapper;
    private VariableMapper variableMapper;

    public ModelELContext(ELContext baseELContext,
                          ModelHolder modelHolder,
                          String modelRootVarName,
                          String modelHolderVarName) {
        this.elResolver = new CompositeELResolver();
        this.elResolver.add(new ModelRootELResolver(modelRootVarName, modelHolder));
        this.elResolver.add(new ModelHolderELResolver(modelHolderVarName, modelHolder));
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
