package at.irian.ankor.change;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class ChangeEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeEvent.class);

    private final String modelContextPath;

    public ChangeEvent(Ref changedProperty) {
        this(changedProperty, null);
    }

    protected ChangeEvent(Ref changedProperty, String modelContextPath) {
        super(changedProperty);
        this.modelContextPath = modelContextPath;
    }

    public Ref getChangedProperty() {
        return (Ref)source;
    }

    public ChangeEvent withModelContextPath(String modelContextPath) {
        return new ChangeEvent(getChangedProperty(), modelContextPath);
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        if (listener instanceof ChangeEventListener) {
            Ref watchedProperty = ((ChangeEventListener) listener).getWatchedProperty();
            Ref changedProperty = getChangedProperty();
            if (watchedProperty == null || watchedProperty.equals(changedProperty) || watchedProperty.isDescendantOf(changedProperty)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        Ref changedProperty = getChangedProperty();
//        if (modelContextPath != null) {
//            changedProperty = changedProperty.withRefContext(changedProperty.getRefContext().withModelContextPath(modelContextPath));
//        }
        ((ChangeEventListener)listener).processChange(changedProperty);
    }

}
