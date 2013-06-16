package at.irian.ankor.impl.el;

import javax.el.ValueExpression;
import javax.el.VariableMapper;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class AnkorVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorVariableMapper.class);

    private final Map<String, ValueExpression> vars = new HashMap<String, ValueExpression>();

    public AnkorVariableMapper(ValueExpression model) {
        setVariable("root", model);
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        return vars.get(variable);
    }

    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        return vars.put(variable, expression);
    }
}
