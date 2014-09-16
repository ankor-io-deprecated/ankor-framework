package at.irian.ankor.viewmodel.listener;

import at.irian.ankor.event.EventListener;
import at.irian.ankor.event.EventListeners;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefContextImplementor;
import at.irian.ankor.viewmodel.ViewModelPostProcessor;
import at.irian.ankor.viewmodel.metadata.ActionListenerMetadata;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

import java.util.Collection;

/**
 * @author Manfred Geiler
 */
public class ActionListenersPostProcessor implements ViewModelPostProcessor {

    @Override
    public void postProcess(Object bean, Ref beanRef, BeanMetadata md) {
        EventListeners eventListeners = ((RefContextImplementor) beanRef.context()).eventListeners();

        // look for a ViewModelActionEventListener bound to the same Ref...
        for (EventListener eventListener : eventListeners) {
            if (eventListener instanceof ViewModelActionEventListener) {
                if (((ViewModelActionEventListener) eventListener).getWatchedProperty().equals(beanRef)) {
                    // ... and discard it
                    eventListeners.remove(eventListener);
                    break;
                }
            }
        }

        Collection<ActionListenerMetadata> actionListeners = md.getActionListeners();
        if (actionListeners != null && !actionListeners.isEmpty()) {
            eventListeners.add(new ViewModelActionEventListener(beanRef, bean, actionListeners));
        }
    }
}
