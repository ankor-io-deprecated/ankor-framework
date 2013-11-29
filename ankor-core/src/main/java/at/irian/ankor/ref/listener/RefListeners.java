package at.irian.ankor.ref.listener;

import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.RefContext;
import at.irian.ankor.ref.impl.RefContextImplementor;

import java.util.ArrayList;

/**
 * Utility class for adding convenient ref listeners to a Ref.
 * These ref listeners are simplified event listeners.
 *
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public final class RefListeners {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RefListeners.class);

    /**
     * no instances
     */
    private RefListeners() {}


    /**
     * Adds a listener, that is invoked on every view model change and automatically discarded when the given Ref
     * gets invalid.
     */
    public static void addChangeListener(Ref ref, RefChangeListener listener) {
        ((RefContextImplementor)ref.context()).eventListeners().add(new RefChangeEventListener(ref, listener));
    }

    /**
     * Adds a listener, that is invoked on every change of the property referenced by the given ref.
     */
    public static void addPropChangeListener(Ref ref, RefChangeListener listener) {
        ((RefContextImplementor)ref.context()).eventListeners().add(new PropRefChangeEventListener(ref, listener));
    }

    /**
     * Adds a listener, that is invoked on every change of the property referenced by the given ref or any other
     * property that is a descendant of the given ref.
     */
    public static void addTreeChangeListener(Ref ref, RefChangeListener listener) {
        ((RefContextImplementor)ref.context()).eventListeners().add(new TreeRefChangeEventListener(ref, listener));
    }

    /**
     * Adds a listener, that is invoked on every action fired on the given ref.
     */
    public static void addPropActionListener(Ref ref, RefActionListener listener) {
        ((RefContextImplementor)ref.context()).eventListeners().add(new PropRefActionEventListener(ref, listener));
    }

    public static void removeListener(Ref ref, RefListener listener) {
        removeListener(ref.context(), listener);
    }

    public static void removeListener(RefContext refContext, RefListener listener) {
        ArrayList<ModelEventListener> eventListenersToRemove = null;
        for (ModelEventListener eventListener : ((RefContextImplementor) refContext).eventListeners()) {
            if (eventListener instanceof RefEventListenerImplementor) {
                if (((RefEventListenerImplementor)eventListener).getRefListener() == listener) {
                    if (eventListenersToRemove == null) {
                        eventListenersToRemove = new ArrayList<ModelEventListener>();
                    }
                    eventListenersToRemove.add(eventListener);
                }
            }
        }

        if (eventListenersToRemove != null) {
            for (ModelEventListener eventListener : eventListenersToRemove) {
                ((RefContextImplementor) refContext).eventListeners().remove(eventListener);
            }
        }

    }
}
