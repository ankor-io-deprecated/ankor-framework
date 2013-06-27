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
    public Ref rootRef() {
        return root(refContext);
    }

    @Override
    public Ref ref(String path) {
        return ref(refContext, path);
    }


    private static Ref root(ELRefContext refContext) {
        return ref(refContext, refContext.getModelRootVarName());
    }

    private static Ref ref(ELRefContext ref2Context, String path) {
        ValueExpression ve = createValueExpressionFor(ref2Context, path);
        return new ELRef(ve, ref2Context);
    }

    private static ValueExpression createValueExpressionFor(ELRefContext ref2Context, String path) {
        return ref2Context.getExpressionFactory().createValueExpression(ref2Context.getElContext(),
                                                                        ELUtils.pathToExpr(path),
                                                                        Object.class);
    }


}
