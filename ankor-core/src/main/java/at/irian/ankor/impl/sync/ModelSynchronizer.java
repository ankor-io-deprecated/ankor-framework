package at.irian.ankor.impl.sync;

import at.irian.ankor.api.application.Application;
import at.irian.ankor.impl.el.AnkorELContext;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
public class ModelSynchronizer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSynchronizer.class);

    private final Application application;

    public ModelSynchronizer(Application application) {
        this.application = application;
    }

    void apply(Object model, ModelChange change) {
        ExpressionFactory expressionFactory = application.getExpressionFactory();
        ValueExpression modelExpr = expressionFactory.createValueExpression(model, model.getClass());
        ELContext elContext = new AnkorELContext(application.getELResolver(), modelExpr);
        String changeExprStr = change.getRef().getValueExpression();
        ValueExpression changeExpr = expressionFactory.createValueExpression(elContext, "#{" + changeExprStr + "}", Object.class);
        changeExpr.setValue(elContext, change.getNewValue());
    }

}
