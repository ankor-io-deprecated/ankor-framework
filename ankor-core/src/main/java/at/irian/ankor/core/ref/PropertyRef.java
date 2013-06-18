package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelChangeWatcher;

import javax.el.ValueExpression;

/**
 * @author Manfred Geiler
 */
public class PropertyRef implements ModelRef {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyRef.class);

    private final RefFactory refFactory;
    private final ValueExpression valueExpression;
    private final ModelChangeWatcher modelChangeWatcher;

    PropertyRef(RefFactory refFactory,
                ValueExpression valueExpression,
                ModelChangeWatcher modelChangeWatcher) {
        this.refFactory = refFactory;
        this.valueExpression = valueExpression;
        this.modelChangeWatcher = modelChangeWatcher;
    }

    public void setValue(Object newValue) {
        Object oldValue = null;
        if (modelChangeWatcher != null) {
            oldValue = valueExpression.getValue(refFactory.elContext());
            modelChangeWatcher.beforeModelChange(this, oldValue, newValue);
        }

        valueExpression.setValue(refFactory.elContext(), newValue);

        if (modelChangeWatcher != null) {
            modelChangeWatcher.afterModelChange(this, oldValue, newValue);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue() {
        return (T)valueExpression.getValue(refFactory.elContext());
    }

    @Override
    public RootRef root() {
        return refFactory.rootRef();
    }

    @Override
    public ModelRef unwatched() {
        return refFactory.unwatched(this);
    }

    @Override
    public void fireAction(String action) {
        refFactory.modelActionBus().broadcastAction(this, action);
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
    public ModelRef sub(String subPath) {
        return refFactory.subRef(this, subPath);
    }

    @Override
    public ModelRef parent() {
        return refFactory.parentRef(this);
    }

    @Override
    public String path() {
        return refFactory.pathOf(this);
    }

    ValueExpression getValueExpression() {
        return valueExpression;
    }

    ModelChangeWatcher getModelChangeWatcher() {
        return modelChangeWatcher;
    }
}
