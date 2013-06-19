package at.irian.ankor.core.ref;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.ModelActionBus;
import at.irian.ankor.core.application.ModelChangeNotifier;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
class PropertyRef implements Ref {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyRef.class);

    private final RefFactory refFactory;
    private final ValueExpression valueExpression;
    private final ModelChangeNotifier modelChangeNotifier;

    PropertyRef(RefFactory refFactory,
                ValueExpression valueExpression,
                ModelChangeNotifier modelChangeNotifier) {
        this.refFactory = refFactory;
        this.valueExpression = valueExpression;
        this.modelChangeNotifier = modelChangeNotifier;
    }

    public void setValue(Object newValue) {
        valueExpression.setValue(refFactory.elContext(), newValue);
        if (modelChangeNotifier != null) {
            modelChangeNotifier.notifyLocalListeners(this);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T)valueExpression.getValue(refFactory.elContext());
    }

    @Override
    public Ref root() {
        return refFactory.rootRef();
    }

    @Override
    public Ref unwatched() {
        return refFactory.unwatched(this);
    }

    @Override
    public void fire(ModelAction action) {
        ModelActionBus modelActionBus = refFactory.modelActionBus();
        if (modelActionBus != null) {
            modelActionBus.broadcastAction(this, action);
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        PropertyRef that = (PropertyRef) o;

        if (!valueExpression.equals(that.valueExpression)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        return valueExpression.hashCode();
    }

    @Override
    public String toString() {
        return refFactory.toString(this);
    }

    @Override
    public Ref sub(String subPath) {
        return refFactory.subRef(this, subPath);
    }

    @Override
    public Ref parent() {
        return refFactory.parentRef(this);
    }

    @Override
    public String path() {
        return refFactory.pathOf(this);
    }

    ValueExpression getValueExpression() {
        return valueExpression;
    }

    ModelChangeNotifier getModelChangeNotifier() {
        return modelChangeNotifier;
    }

    @Override
    public boolean isDescendantOf(Ref ref) {
        Ref parentRef = parent();
        return parentRef.equals(ref) || parentRef.isDescendantOf(ref);
    }

    @Override
    public boolean isAncestorOf(Ref ref) {
        return ref.isDescendantOf(this);
    }
}
