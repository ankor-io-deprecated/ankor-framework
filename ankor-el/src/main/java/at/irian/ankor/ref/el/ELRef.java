package at.irian.ankor.ref.el;

import at.irian.ankor.action.Action;
import at.irian.ankor.event.ActionListener;
import at.irian.ankor.event.ActionNotifier;
import at.irian.ankor.event.ChangeListener;
import at.irian.ankor.event.ChangeNotifier;
import at.irian.ankor.ref.BaseRef;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
class ELRef extends BaseRef {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRef.class);

    private final ValueExpression valueExpression;
    private final boolean deleted;

    ELRef(ELRefContext refContext, ValueExpression valueExpression, boolean deleted) {
        super(refContext);
        this.valueExpression = valueExpression;
        this.deleted = deleted;
    }

    @Override
    public void setValue(Object newValue) {
        valueExpression.setValue(refContext().getELContext(), newValue);
        ChangeNotifier changeNotifier = refContext().getChangeNotifier();
        if (changeNotifier != null) {
            changeNotifier.broadcastChange(refContext.getModelContext(), this);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T)valueExpression.getValue(refContext().getELContext());
    }

    @Override
    public Ref delete() {
        valueExpression.setValue(refContext().getELContext(), null);
        ELRef deletedRef = new ELRef(refContext(), valueExpression, true);

        ChangeNotifier changeNotifier = refContext().getChangeNotifier();
        if (changeNotifier != null) {
            changeNotifier.broadcastChange(refContext.getModelContext(), deletedRef);
        }

        return deletedRef;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public Ref root() {
        return ELRefUtils.rootRef(refContext(), deleted);
    }

    @Override
    public Ref unwatched() {
        return new ELRef(refContext().withNoModelChangeNotifier(), valueExpression, deleted);
    }

    @Override
    public void fire(Action action) {
        ActionNotifier actionNotifier = refContext().getActionNotifier();
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
    public Ref sub(String subPath) {
        return ELRefUtils.subRef(this, subPath, deleted);
    }

    @Override
    public Ref sub(int index) {
        return ELRefUtils.subRef(this, index, deleted);
    }

    @Override
    public Ref parent() {
        return ELRefUtils.parentRef(this, deleted);
    }

    @Override
    public boolean isRoot() {
        return ELRefUtils.isRootPath(refContext(), path());
    }

    @Override
    public String path() {
        return ELRefUtils.path(this);
    }

    @Override
    public ELRefContext refContext() {
        return (ELRefContext)refContext;
    }

    ValueExpression valueExpression() {
        return valueExpression;
    }

    @Override
    public Ref withRefContext(RefContext newRefContext) {
        return ELRefUtils.ref((ELRefContext)newRefContext, path(), deleted);
    }

    @Override
    public void registerRemoteChangeListener(ChangeListener listener) {
        refContext().getListenerRegistry().registerRemoteChangeListener(this, listener);
    }

    @Override
    public void registerActionListener(ActionListener listener) {
        refContext().getListenerRegistry().registerRemoteActionListener(this, listener);
    }
}
