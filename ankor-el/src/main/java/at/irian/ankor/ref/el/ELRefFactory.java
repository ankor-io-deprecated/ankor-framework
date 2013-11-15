package at.irian.ankor.ref.el;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefFactory;

/**
 * @author Manfred Geiler
 */
public class ELRefFactory implements RefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRefFactory.class);

    private ELRefContext elRefContext;

    protected ELRefFactory() {}

    protected void setRefContext(ELRefContext elRefContext) {
        this.elRefContext = elRefContext;
    }

    @Override
    public Ref ref(String path) {
        return new ELRef(elRefContext, ValueExpressionHelper.createValueExpression(elRefContext, path));
    }

}
