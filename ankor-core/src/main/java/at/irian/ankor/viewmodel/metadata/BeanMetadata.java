package at.irian.ankor.viewmodel.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author Manfred Geiler
 */
public class BeanMetadata {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BeanMetadata.class);

    private final Collection<ChangeListenerMetadata> changeListeners;
    private final Collection<ActionListenerMetadata> actionListeners;
    private final Collection<WatchedPropertyMetadata> watchedProperties;

    public BeanMetadata() {
        this(null, null, null);
    }

    protected BeanMetadata(Collection<ChangeListenerMetadata> changeListeners,
                           Collection<ActionListenerMetadata> actionListeners,
                           Collection<WatchedPropertyMetadata> watchedProperties) {
        this.changeListeners = changeListeners;
        this.actionListeners = actionListeners;
        this.watchedProperties = watchedProperties;
    }


    protected static <E> Collection<E> combine(Collection<E> c1, Collection<E> c2) {
        if (c1 == null || c1.isEmpty()) {
            return c2;
        } else if (c2 == null || c2.isEmpty()) {
            return c1;
        } else {
            Collection<E> c = new ArrayList<E>(c1);
            c.addAll(c2);
            return c;
        }
    }


    public BeanMetadata withChangeListeners(Collection<ChangeListenerMetadata> changeListeners) {
        return new BeanMetadata(combine(this.changeListeners, changeListeners), actionListeners, watchedProperties);
    }

    public BeanMetadata withActionListeners(Collection<ActionListenerMetadata> actionListeners) {
        return new BeanMetadata(changeListeners, combine(this.actionListeners, actionListeners), watchedProperties);
    }

    public BeanMetadata withWatchedProperties(Collection<WatchedPropertyMetadata> watchedProperties) {
        return new BeanMetadata(changeListeners, actionListeners, combine(this.watchedProperties, watchedProperties));
    }



    public Collection<ChangeListenerMetadata> getChangeListeners() {
        return changeListeners != null ? changeListeners : Collections.<ChangeListenerMetadata>emptyList();
    }

    public Collection<ActionListenerMetadata> getActionListeners() {
        return actionListeners != null ? actionListeners : Collections.<ActionListenerMetadata>emptyList();
    }

    public Collection<WatchedPropertyMetadata> getWatchedProperties() {
        return watchedProperties != null ? watchedProperties : Collections.<WatchedPropertyMetadata>emptyList();
    }
}
