package at.irian.ankor.ref.el;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.el.ELUtils;
import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.path.PathSyntax;
import at.irian.ankor.ref.*;

import javax.el.ValueExpression;
import javax.el.ValueReference;

/**
 * @author MGeiler (Manfred Geiler)
 */
class ELRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ELRef.class);

    private final ValueExpression ve;
    private final ELRefContext refContext;

    ELRef(ValueExpression ve, ELRefContext refContext) {
        this.ve = ve;
        this.refContext = refContext;
    }

    @Override
    public void setValue(Object newValue) {
        Object oldValue;
        try {
            oldValue = getValue();
        } catch (Exception e) {
            throw new IllegalArgumentException("invalid ref", e);
        }
//            if (newValue != oldValue) {
//                // todo: is this ok?
        ve.setValue(refContext.getElContext(), newValue);
        refContext.getEventBus().fire(new ChangeEvent(this));
//            }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue() {
        return (T)ve.getValue(refContext.getElContext());
    }

    @Override
    public boolean isValid() {
        if (isRoot()) {
            Object modelRoot = ve.getValue(refContext.getElContext());
            return modelRoot != null;
        } else {
            ValueReference valueReference = ve.getValueReference(refContext.getElContext());
            return valueReference != null && valueReference.getBase() != null;
        }
    }

    @Override
    public Ref root() {
        return refFactory().rootRef();
    }

    @Override
    public Ref parent() {
        if (isRoot()) {
            throw new UnsupportedOperationException("root ref has no parent");
        } else {
            return refFactory().ref(pathSyntax().parentOf(path()));
        }
    }

    private PathSyntax pathSyntax() {return refContext.getPathSyntax();}

    private RefFactory refFactory() {return refContext.getRefFactory();}

    @Override
    public Ref append(String propertyOrSubPath) {
        return refFactory().ref(pathSyntax().concat(path(), propertyOrSubPath));
    }

    @Override
    public Ref appendIdx(int index) {
        return refFactory().ref(pathSyntax().addArrayIdx(path(), index));
    }

    @Override
    public Ref appendLiteralKey(String literalKey) {
        return refFactory().ref(pathSyntax().addLiteralMapKey(path(), literalKey));
    }

    @Override
    public Ref appendPathKey(String pathKey) {
        return refFactory().ref(pathSyntax().addMapKey(path(), pathKey));
    }

    @Override
    public void fire(ModelEvent event) {
        refContext.getEventBus().fire(event);
    }

    @Override
    public void fireAction(Action action) {
        refContext.getEventBus().fire(new ActionEvent(this, action));
    }

    @Override
    public String path() {
        return ELUtils.exprToPath(expression());
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        if (isRoot()) {
            return false;
        }
        Ref parentRef = parent();
        return parentRef != null && (parentRef.equals(ref) || parentRef.isDescendantOf(ref));
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return ref.isDescendantOf(this);
    }

    @Override
    public boolean isRoot() {
        return path().equals(refContext.getModelRootVarName());
    }

    @Override
    public RefContext getRefContext() {
        return refContext;
    }

    @Override
    public Ref withRefContext(RefContext newRefContext) {
        return new ELRef(ve, (ELRefContext)newRefContext);
    }

    @Override
    public void addChangeListener(final ChangeListener listener) {
        ChangeEventListener eventListener = new ChangeEventListener(this) {
            @Override
            public void processChange(Ref changedProperty) {
                listener.processChange(changedProperty, getWatchedProperty());
            }
        };
        refContext.getEventBus().addListener(eventListener);
    }

    @Override
    public void addActionListener(final ActionListener listener) {
        ActionEventListener eventListener = new ActionEventListener(this) {
            @Override
            public void processAction(Ref actionProperty, Action action) {
                listener.processAction(getWatchedProperty(), action);
            }
        };
        refContext.getEventBus().addListener(eventListener);
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
