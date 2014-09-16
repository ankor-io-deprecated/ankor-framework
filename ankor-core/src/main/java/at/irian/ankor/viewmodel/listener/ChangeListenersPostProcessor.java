package at.irian.ankor.viewmodel.listener;

import at.irian.ankor.event.EventListener;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;
import at.irian.ankor.viewmodel.metadata.ChangeListenerMetadata;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class ChangeListenersPostProcessor implements ViewModelPostProcessor {

    @Override
    public void postProcess(Object bean, Ref beanRef, BeanMetadata md) {
        EventListeners eventListeners = ((RefContextImplementor) beanRef.context()).eventListeners();

        // look for a ViewModelChangeEventListener bound to the same Ref...
        for (EventListener eventListener : eventListeners) {
            if (eventListener instanceof ViewModelChangeEventListener) {
                if (((ViewModelChangeEventListener) eventListener).getViewModelBeanRef().equals(beanRef)) {
                    // ... and discard it
                    eventListeners.remove(eventListener);
                    break;
                }
            }
        }

        Collection<ChangeListenerMetadata> changeListeners = md.getChangeListeners();
        if (changeListeners != null && !changeListeners.isEmpty()) {
            eventListeners.add(new ViewModelChangeEventListener(beanRef, bean, changeListeners));
        }
    }
}
