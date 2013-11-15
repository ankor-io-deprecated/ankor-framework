package at.irian.ankor.ref.el;

import at.irian.ankor.el.ELUtils;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
public class ValueExpressionHelper {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ValueExpressionHelper.class);

    public static ValueExpression createValueExpression(ELRefContext elRefContext, String path) {
        return elRefContext.getExpressionFactory().createValueExpression(elRefContext.createELContext(),
                                                                         ELUtils.pathToExpr(path),
                                                                         Object.class);
    }

}
