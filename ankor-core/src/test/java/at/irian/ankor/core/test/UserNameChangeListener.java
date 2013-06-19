package at.irian.ankor.core.test;

import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class UserNameChangeListener implements ModelChangeListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserNameChangeListener.class);

    @Override
    public void handleModelChange(Ref watchedRef, Ref changedRef) {
        LOG.debug("after change {}, new: {}", watchedRef, watchedRef.getValue());
    }
}
