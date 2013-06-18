package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelHolder;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
class ModelHolderVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderVariableMapper.class);

    static final String MODEL_ROOT_VAR_NAME = "model";

    private final ExpressionFactory expressionFactory;
    private final ELContext baseELContext;
    private final ModelHolder modelHolder;

    public ModelHolderVariableMapper(ExpressionFactory expressionFactory,
                                     ELContext baseELContext,
                                     ModelHolder modelHolder) {
        this.baseELContext = baseELContext;
        this.expressionFactory = expressionFactory;
        this.modelHolder = modelHolder;
    }

    @Override
    public ValueExpression resolveVariable(String variable) {
        if (MODEL_ROOT_VAR_NAME.equals(variable)) {
            return expressionFactory.createValueExpression(modelHolder.getModel(), modelHolder.getModelType());
        } else {
            return baseELContext.getVariableMapper().resolveVariable(variable);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (MODEL_ROOT_VAR_NAME.equals(variable)) {
            Object oldModel = modelHolder.getModel();
            Object newModel = expression.getValue(baseELContext);
            modelHolder.setModel(newModel);
            return expressionFactory.createValueExpression(oldModel, modelHolder.getModelType());
        } else {
            return baseELContext.getVariableMapper().setVariable(variable, expression);
        }
    }
}
