package at.irian.ankor.core.el;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
public class BeanResolverVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderVariableMapper.class);

    private final ExpressionFactory expressionFactory;
    private final ELContext baseELContext;
    private final BeanResolver beanResolver;

    public BeanResolverVariableMapper(ExpressionFactory expressionFactory,
                                      ELContext baseELContext,
                                      BeanResolver beanResolver) {
        this.baseELContext = baseELContext;
        this.expressionFactory = expressionFactory;
        this.beanResolver = beanResolver;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        Object bean = beanResolver.resolveByName(variable);
        if (bean != null) {
            return expressionFactory.createValueExpression(bean, bean.getClass());
        } else {
            return baseELContext.getVariableMapper().resolveVariable(variable);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        Object bean = beanResolver.resolveByName(variable);
        if (bean != null) {
            throw new UnsupportedOperationException();
        } else {
            return baseELContext.getVariableMapper().setVariable(variable, expression);
        }
    }
}
