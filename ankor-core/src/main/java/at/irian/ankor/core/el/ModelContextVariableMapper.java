package at.irian.ankor.core.el;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
public class ModelContextVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderVariableMapper.class);

    private static final String CONTEXT_VAR_NAME = "context";

    private final ELContext baseELContext;
    private final ValueExpression contextValueExpression;

    public ModelContextVariableMapper(ELContext baseELContext, ValueExpression contextValueExpression) {
        this.baseELContext = baseELContext;
        this.contextValueExpression = contextValueExpression;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        if (CONTEXT_VAR_NAME.equals(variable)) {
            return contextValueExpression;
        } else {
            return baseELContext.getVariableMapper().resolveVariable(variable);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (CONTEXT_VAR_NAME.equals(variable)) {
            throw new UnsupportedOperationException("cannot change context");
        } else {
            return baseELContext.getVariableMapper().setVariable(variable, expression);
        }
    }
}
