package at.irian.ankor.monitor.stats;

import at.irian.ankor.monitor.ModelSessionMonitor;
import at.irian.ankor.session.ModelSession;

/**
 * @author Manfred Geiler
 */
public class StatsModelSessionMonitor implements ModelSessionMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatsModelSessionMonitor.class);

    private final ModelSessionStats stats;

    public StatsModelSessionMonitor(ModelSessionStats stats) {
        this.stats = stats;
    }

    @Override
    public void monitor_create(ModelSession modelSession) {
        stats.incrementOpenSessions();
    }

    @Override
    public void monitor_close(ModelSession modelSession) {
        stats.decrementOpenSessions();
    }
}
