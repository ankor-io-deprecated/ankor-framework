package at.irian.ankor.ref.el;

import at.irian.ankor.el.ELUtils;
import at.irian.ankor.event.dispatch.DispatchThreadChecker;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.InvalidRefException;
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

        // check if we are correctly running in the correct event dispatcher thread
        new DispatchThreadChecker(context().modelSession()).check();

        try {
            ve.setValue(elRefContext().createELContext(), newValue);
        } catch (PropertyNotFoundException e) {
            if (isRoot()) {
                LOG.debug("Root property with name '{}' not found - setting model root in application instance", propertyName());
                context().modelSession().getApplicationInstance().setModelRoot(propertyName(), newValue);
            } else {
                throw e;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T> T internalGetValue() throws InvalidRefException {
        try {
            return (T)ve.getValue(elRefContext().createELContext());
        } catch (PropertyNotFoundException e) {
            throw new InvalidRefException(this);
        } catch (IllegalStateException e) {
            LOG.warn("unable to get value of " + this, e);
            return null;
        }
    }

    @Override
    public Class<?> getType() {
        try {
            return ve.getType(elRefContext().createELContext());
        } catch (PropertyNotFoundException e) {
            return null;
        }
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
        if (this == that) {
            return true;
        }

        if (that == null || !(that instanceof Ref)) {
            return false;
        }

        String thisPath = this.path();
        String thatPath = ((Ref) that).path();

        return context().pathSyntax().isEqual(thisPath, thatPath);
    }

    private String expression() {
        return ve.getExpressionString();
    }

    @Override
    public int hashCode() {
        return expression().hashCode();
    }

}
