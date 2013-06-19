package at.irian.ankor.core.el;

import at.irian.ankor.core.application.ModelHolder;
import at.irian.ankor.core.util.NilValue;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.el.VariableMapper;

/**
 * @author Manfred Geiler
 */
public class ModelHolderVariableMapper extends VariableMapper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelHolderVariableMapper.class);

    public static final String ROOT_VAR_NAME = "root";
    public static final String MODEL_HOLDER_VAR_NAME = "modelHolder";

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
        if (ROOT_VAR_NAME.equals(variable)) {
            Object model = modelHolder.getModel();
            return expressionFactory.createValueExpression(model, modelHolder.getModelType());
        } else if (MODEL_HOLDER_VAR_NAME.equals(variable)) {
                return expressionFactory.createValueExpression(modelHolder, ModelHolder.class);
        } else {
            return baseELContext.getVariableMapper().resolveVariable(variable);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public ValueExpression setVariable(String variable, ValueExpression expression) {
        if (ROOT_VAR_NAME.equals(variable)) {
            Object oldModel = modelHolder.getModel();
            Object newModel = expression.getValue(baseELContext);
            modelHolder.setModel(newModel);
            return expressionFactory.createValueExpression(oldModel, modelHolder.getModelType());
        } else if (MODEL_HOLDER_VAR_NAME.equals(variable)) {
            throw new UnsupportedOperationException("cannot change modelHolder");
        } else {
            return baseELContext.getVariableMapper().setVariable(variable, expression);
        }
    }
}
