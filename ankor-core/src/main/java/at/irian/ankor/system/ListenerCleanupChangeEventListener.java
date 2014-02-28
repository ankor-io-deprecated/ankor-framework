package at.irian.ankor.system;

import at.irian.ankor.change.Change;
import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;
import at.irian.ankor.ref.Ref;

/**
 * Global ChangeEventListener that automatically discards all ModelEventListeners that are no longer owned by valid
 * model Ref after a model property's value has changed to <code>null</code>.
 *
 * @author Manfred Geiler
 */
public class ListenerCleanupChangeEventListener extends ChangeEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerCleanupChangeEventListener.class);

    public ListenerCleanupChangeEventListener() {
        super(null); // global listener
    }

    @Override
    public boolean isDiscardable() {
        return false; // this is a global system listener
    }

    @Override
    public void process(ChangeEvent event) {
        Change change = event.getChange();
        switch (change.getType()) {
            case value:
                if (change.getValue() == null) {
                    cleanupListeners(event);
                }
                break;
            case delete:
                cleanupListeners(event);
                break;
            case insert:
                // no cleanup necessary
                break;
            case replace:
                cleanupListeners(event);
                break;
            default:
                throw new IllegalArgumentException("unsupported change type " + change.getType().name());
        }
    }

    private void cleanupListeners(ChangeEvent event) {
        Ref changedProperty = event.getChangedProperty();
        if (changedProperty != null) {
            changedProperty.context().modelSession().getEventListeners().cleanup();
        }
    }
}
