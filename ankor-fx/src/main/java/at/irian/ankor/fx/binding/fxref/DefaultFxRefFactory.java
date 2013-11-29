package at.irian.ankor.fx.binding.fxref;

import at.irian.ankor.ref.el.ValueExpressionHelper;

/**
 * @author Manfred Geiler
 */
class DefaultFxRefFactory implements FxRefFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultFxRefFactory.class);

    private DefaultFxRefContext refContext;

    protected DefaultFxRefFactory() {}

    protected void setRefContext(DefaultFxRefContext refContext) {
        this.refContext = refContext;
    }

    @Override
    public FxRef ref(String path) {
        return new DefaultFxRef(refContext, ValueExpressionHelper.createValueExpression(refContext, path));
    }

}
