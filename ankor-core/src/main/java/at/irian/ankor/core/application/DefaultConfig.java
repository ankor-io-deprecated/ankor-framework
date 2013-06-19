package at.irian.ankor.core.application;

/**
 * @author Manfred Geiler
 */
public class DefaultConfig implements Config {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultConfig.class);

    private static final String DEFAULT_MODEL_ROOT_VAR_NAME = "root";
    private static final String DEFAULT_MODEL_HOLDER_VAR_NAME = "modelHolder";
    private static final String DEFAULT_CONTEXT_VAR_NAME = "context";

    @Override
    public String getModelRootVarName() {
        return DEFAULT_MODEL_ROOT_VAR_NAME;
    }

    @Override
    public String getModelHolderVarName() {
        return DEFAULT_MODEL_HOLDER_VAR_NAME;
    }

    @Override
    public String getContextVarName() {
        return DEFAULT_CONTEXT_VAR_NAME;
    }
}
