package at.irian.ankor.el;

import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
class StandardVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderVariableMapper.class);

    @Override
    public ValueExpression resolveVariable(String variable) {
        return null;
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        return null;
    }
}
