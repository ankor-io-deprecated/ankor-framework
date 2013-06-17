package at.irian.ankor.core.ref;

import at.irian.ankor.core.application.ModelChangeWatcher;
import at.irian.ankor.core.model.ModelHolder;

/**
 * @author Manfred Geiler
 */
public class RootRef implements ModelRef {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PropertyRef.class);

    private final RefFactory refFactory;
    private final ModelHolder modelHolder;
    private final ModelChangeWatcher modelChangeWatcher;

    RootRef(RefFactory refFactory,
            ModelHolder modelHolder,
            ModelChangeWatcher modelChangeWatcher) {
        this.refFactory = refFactory;
        this.modelHolder = modelHolder;
        this.modelChangeWatcher = modelChangeWatcher;
    }

    @Override
    public RootRef root() {
        return this;
    }

    @Override
    public ModelRef parent() {
        return null;
    }

    @Override
    public ModelRef unwatched() {
        return refFactory.unwatchedRootRef();
    }

    public void setValue(Object newValue) {
        Object oldValue = null;
        if (modelChangeWatcher != null) {
            oldValue = modelHolder.getModel();
            modelChangeWatcher.beforeModelChange(this, oldValue, newValue);
        }

        this.modelHolder.setModel(newValue);

        if (this.modelChangeWatcher != null) {
            this.modelChangeWatcher.afterModelChange(this, oldValue, newValue);
        }
    }

    @SuppressWarnings("RedundantCast")
    public <T> T getValue() {
        return (T)this.modelHolder.getModel();
    }

    @Override
    public void fireAction(String action) {
        refFactory.modelActionBus().broadcastAction(this, action);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return refFactory.toString(this);
    }

    @Override
    public ModelRef sub(String subPath) {
        return refFactory.ref(subPath, modelChangeWatcher);
    }

    @Override
    public String toPath() {
        return refFactory.toPath(this);
    }
}
