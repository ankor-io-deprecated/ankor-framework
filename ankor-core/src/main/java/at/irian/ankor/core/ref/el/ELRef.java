package at.irian.ankor.core.ref.el;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.DefaultActionNotifier;
import at.irian.ankor.core.ref.Ref;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
class ELRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRef.class);

    private final ELRefContext refContext;
    private final ValueExpression valueExpression;

    ELRef(ELRefContext refContext, ValueExpression valueExpression) {
        this.refContext = refContext;
        this.valueExpression = valueExpression;
    }

    public void setValue(Object newValue) {
        valueExpression.setValue(refContext.getELContext(), newValue);
        if (refContext.getChangeNotifier() != null) {
            refContext.getChangeNotifier().notifyLocalListeners(this);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T)valueExpression.getValue(refContext.getELContext());
    }

    @Override
    public Ref root() {
        return ELRefUtils.rootRef(refContext);
    }

    @Override
    public Ref unwatched() {
        return new ELRef(refContext.withNoModelChangeNotifier(), valueExpression);
    }

    @Override
    public void fire(ModelAction action) {
        DefaultActionNotifier actionNotifier = refContext.getActionNotifier();
        if (actionNotifier != null) {
            actionNotifier.broadcastAction(this, action);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ELRef elRef = (ELRef) o;
        return valueExpression.equals(elRef.valueExpression);

    }

    @Override
    public int hashCode() {
        return valueExpression.hashCode();
    }

    @Override
    public String toString() {
        return "Ref{" + path() + '}';
    }

    @Override
    public Ref sub(String subPath) {
        return ELRefUtils.subRef(this, subPath);
    }

    @Override
    public Ref parent() {
        return ELRefUtils.parentRef(this);
    }

    @Override
    public boolean isRoot() {
        return ELRefUtils.isRootPath(refContext, path());
    }

    @Override
    public String path() {
        return ELRefUtils.path(this);
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        Ref parentRef = parent();
        return parentRef != null && (parentRef.equals(ref) || parentRef.isDescendantOf(ref));
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return ref.isDescendantOf(this);
    }

    @Override
    public ELRefContext refContext() {
        return refContext;
    }

    ValueExpression valueExpression() {
        return valueExpression;
    }
}
