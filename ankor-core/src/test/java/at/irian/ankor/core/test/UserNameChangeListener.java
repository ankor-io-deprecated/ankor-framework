package at.irian.ankor.core.test;

import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class UserNameChangeListener implements ModelChangeListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserNameChangeListener.class);

    @Override
    public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        LOG.debug("before change {}, old: {}, new: {}", modelRef, oldValue, newValue);
    }

    @Override
    public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        LOG.debug("after change {}, old: {}, new: {}", modelRef, oldValue, newValue);
    }
}
