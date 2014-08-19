package at.irian.ankor.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Manfred Geiler
 */
public class ArrayListEventListeners implements EventListeners {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UnsynchronizedListenersHolder.class);

    private final EventListeners parentListeners;
    private final List<EventListener> listeners;

    public ArrayListEventListeners() {
        this(null);
    }

    public ArrayListEventListeners(EventListeners parentListeners) {
        this.parentListeners = parentListeners;
        this.listeners = new CopyOnWriteArrayList<EventListener>();
    }

    @Override
    public void add(EventListener listener) {
        listeners.add(listener);
    }

    @Override
    public void remove(EventListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Iterator<EventListener> iterator() {
        if (parentListeners != null) {
            final Iterator<EventListener> parentIterator = parentListeners.iterator();
            final Iterator<EventListener> thisIterator = listeners.iterator();
            return new Iterator<EventListener>() {

                private int internalState = 0;

                @Override
                public boolean hasNext() {
                    return parentIterator.hasNext() || thisIterator.hasNext();
                }

                @Override
                public EventListener next() {
                    if (parentIterator.hasNext()) {
                        internalState = 1;
                        return parentIterator.next();
                    } else {
                        internalState = 2;
                        return thisIterator.next();
                    }
                }

                @Override
                public void remove() {
                    if (internalState == 1) {
                        internalState = 0;
                        parentIterator.remove();
                    } else if (internalState == 2) {
                        internalState = 0;
                        thisIterator.remove();
                    } else {
                        throw new IllegalStateException();
                    }
                }
            };
        } else {
            return listeners.iterator();
        }
    }

    @Override
    public void cleanup() {
        List<EventListener> removeList = null;
        for (EventListener listener : this.listeners) {
            if (listener != null && listener.isDiscardable()) {  // yes, the CopyOnWriteArrayList actually may return nulls!
                if (removeList == null) {
                    removeList = new ArrayList<EventListener>();
                }
                removeList.add(listener);
            }
        }
        if (removeList != null) {
            for (EventListener eventListener : removeList) {
                this.listeners.remove(eventListener);
            }
        }
    }

}
