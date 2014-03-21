package at.irian.ankor.monitor.stats;

import at.irian.ankor.monitor.AnkorSystemMonitor;
import at.irian.ankor.monitor.ModelSessionMonitor;
import at.irian.ankor.monitor.SwitchboardMonitor;

/**
 * @author Manfred Geiler
 */
public class StatsAnkorSystemMonitor implements AnkorSystemMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(StatsAnkorSystemMonitor.class);

    private final SwitchboardMonitor switchboardMonitor;
    private final ModelSessionMonitor modelSessionMonitor;

    public StatsAnkorSystemMonitor(AnkorSystemStats stats) {
        this.switchboardMonitor = new StatsSwitchboardMonitor(stats.switchboard());
        this.modelSessionMonitor = new StatsModelSessionMonitor(stats.modelSession());
    }

    @Override
    public SwitchboardMonitor switchboard() {
        return switchboardMonitor;
    }

    @Override
    public ModelSessionMonitor modelSession() {
        return modelSessionMonitor;
    }

 }
