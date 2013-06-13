package at.irian.ankor.api.event;

import at.irian.ankor.api.action.ModelActionListener;
import at.irian.ankor.api.model.ModelChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public class ListenerRegistry {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ListenerRegistry.class);

    private final List<Object> listeners = new ArrayList<Object>();
    private final List<ModelActionListener> actionListeners = new ArrayList<ModelActionListener>();

    public void addListener(Object listener) {
        listeners.add(listener);
        if (listener instanceof ModelActionListener) {
            actionListeners.add((ModelActionListener) listener);
        }
    }

    public void removeListener(Object listener) {
        listeners.remove(listener);
        if (listener instanceof ModelActionListener) {
            actionListeners.remove(listener);
        }
    }

    public Collection<ModelChangeListener> getChangeListeners() {
        throw new UnsupportedOperationException();
    }

    public Collection<ModelActionListener> getActionListeners() {
        return Collections.unmodifiableCollection(actionListeners);
    }

}
