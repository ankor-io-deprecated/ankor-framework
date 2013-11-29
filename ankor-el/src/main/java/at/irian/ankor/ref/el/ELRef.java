package at.irian.ankor.ref.el;

import at.irian.ankor.el.ELUtils;
import at.irian.ankor.event.dispatch.DispatchThreadChecker;
import at.irian.ankor.ref.impl.RefBase;

import javax.el.PropertyNotFoundException;
import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
public class ELRef extends RefBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRef.class);

    private final ValueExpression ve;

    protected ELRef(ELRefContext refContext, ValueExpression ve) {
        super(refContext);
        this.ve = ve;
    }

    @Override
    public void internalSetValue(Object newValue) {

        // check if we are correctly running in an event dispatcher thread
        new DispatchThreadChecker(context().modelContext()).check();

        ve.setValue(elRefContext().createELContext(), newValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T internalGetValue() {
        try {
            return (T)ve.getValue(elRefContext().createELContext());
        } catch (IllegalStateException e) {
            LOG.warn("unable to get value of " + this, e);
            return null;
        }
    }

    @Override
    protected Class<?> getType() {
        return ve.getType(elRefContext().createELContext());
    }

    @Override
    public boolean isValid() {
        try {
            ve.getValueReference(elRefContext().createELContext());
        } catch (PropertyNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public String path() {
        return ELUtils.exprToPath(expression());
    }

    @Override
    public boolean isRoot() {
        return !context().pathSyntax().isHasParent(path());
    }

    protected ELRefContext elRefContext() {
        return (ELRefContext)super.context();
    }

    @Override
    public boolean equals(Object that) {
        return this == that
               || !(that == null || getClass() != that.getClass()) && expression().equals(((ELRef) that).expression());
    }

    private String expression() {
        return ve.getExpressionString();
    }

    @Override
    public int hashCode() {
        return expression().hashCode();
    }

}
