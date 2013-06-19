package at.irian.ankor.core.el;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
public class SimpleReadonlySingletonVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderVariableMapper.class);

    private final VariableMapper baseVariableMapper;
    private String singletonName;
    private final ValueExpression singletonValueExpression;

    public SimpleReadonlySingletonVariableMapper(VariableMapper baseVariableMapper,
                                                 String singletonName,
                                                 ValueExpression singletonValueExpression) {

        this.baseVariableMapper = baseVariableMapper;
        this.singletonValueExpression = singletonValueExpression;
        this.singletonName = singletonName;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        if (variable.equals(singletonName)) {
            return singletonValueExpression;
        } else {
            return baseVariableMapper.resolveVariable(variable);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (variable.equals(singletonName)) {
            throw new UnsupportedOperationException("cannot set " + singletonName);
        } else {
            return baseVariableMapper.setVariable(variable, expression);
        }
    }
}
