package at.irian.ankor.viewmodel.listener;

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
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ActionListenersPostProcessor.class);

    @Override
    public void postProcess(Object bean, Ref ref, BeanMetadata md) {
        EventListeners eventListeners = ((RefContextImplementor) ref.context()).eventListeners();

        Collection<ChangeListenerMetadata> changeListeners = md.getChangeListeners();
        if (changeListeners != null && !changeListeners.isEmpty()) {
            eventListeners.add(new ViewModelChangeEventListener(ref, bean, changeListeners));
        }
    }
}
