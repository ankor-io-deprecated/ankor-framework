package at.irian.ankor.api.model;

import javax.el.ValueExpression;

/**
 */
public class ModelChange {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelChange.class);

    private final ValueExpression modelPropertyExpression;
    private final Object newValue;

    public ModelChange(ValueExpression modelPropertyExpression, Object newValue) {
        this.modelPropertyExpression = modelPropertyExpression;
        this.newValue = newValue;
    }

    public ValueExpression getModelPropertyExpression() {
        return modelPropertyExpression;
    }

    public Object getNewValue() {
        return newValue;
    }
}
