package at.irian.ankor.viewmodel.listener;

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
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionListenersPostProcessor.class);

    @Override
    public void postProcess(Object bean, Ref ref, BeanMetadata md) {
        EventListeners eventListeners = ((RefContextImplementor) ref.context()).eventListeners();

        Collection<ActionListenerMetadata> actionListeners = md.getActionListeners();
        if (actionListeners != null && !actionListeners.isEmpty()) {
            eventListeners.add(new ViewModelActionEventListener(ref, bean, actionListeners));
        }
    }
}
