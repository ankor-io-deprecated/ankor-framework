package at.irian.ankor.viewmodel;

import at.irian.ankor.event.EventListeners;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.viewmodel.listener.ViewModelActionEventListener;
import at.irian.ankor.viewmodel.listener.ViewModelChangeEventListener;
import at.irian.ankor.viewmodel.metadata.ActionListenerMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.ChangeListenerMetadata;
import at.irian.ankor.viewmodel.metadata.WatchedPropertyMetadata;
import at.irian.ankor.viewmodel.watch.WatchedPropertyInitializer;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class ViewModelBeanInitializer {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelBeanInitializer.class);

    public void init(Object bean, Ref ref, BeanMetadata md) {

        EventListeners eventListeners = ((RefContextImplementor) ref.context()).eventListeners();

        Collection<ActionListenerMetadata> actionListeners = md.getActionListeners();
        if (actionListeners != null && !actionListeners.isEmpty()) {
            eventListeners.add(new ViewModelActionEventListener(ref, bean, actionListeners));
        }

        Collection<ChangeListenerMetadata> changeListeners = md.getChangeListeners();
        if (changeListeners != null && !changeListeners.isEmpty()) {
            eventListeners.add(new ViewModelChangeEventListener(ref, bean, changeListeners));
        }

        Collection<WatchedPropertyMetadata> watchedProperties = md.getWatchedProperties();
        for (WatchedPropertyMetadata watchedPropertyMetadata : watchedProperties) {
            new WatchedPropertyInitializer().init(bean, ref, watchedPropertyMetadata);
        }

    }

}
