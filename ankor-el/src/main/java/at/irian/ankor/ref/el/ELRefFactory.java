package at.irian.ankor.ref.el;

import at.irian.ankor.el.ELUtils;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
public class ELRefFactory implements RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefFactory.class);

    private final ELRefContext refContext;

    ELRefFactory(ELRefContext refContext) {
        this.refContext = refContext;
    }

    @Override
    public Ref ref(String path) {
        return ref(refContext, path);
    }


    private static Ref ref(ELRefContext elRefContext, String path) {
        ValueExpression ve = createValueExpressionFor(elRefContext, path);
        return new ELRef(elRefContext, ve);
    }

    private static ValueExpression createValueExpressionFor(ELRefContext elRefContext, String path) {
        return elRefContext.getExpressionFactory().createValueExpression(elRefContext.createELContext(),
                                                                         ELUtils.pathToExpr(path),
                                                                         Object.class);
    }


}
