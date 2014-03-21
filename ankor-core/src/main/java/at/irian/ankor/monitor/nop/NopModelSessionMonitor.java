package at.irian.ankor.monitor.nop;

import at.irian.ankor.monitor.ModelSessionMonitor;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class NopModelSessionMonitor implements ModelSessionMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NopModelSessionMonitor.class);

    @Override
    public void monitor_create(ModelSession modelSession) {

    }

    @Override
    public void monitor_close(ModelSession modelSession) {

    }
}
