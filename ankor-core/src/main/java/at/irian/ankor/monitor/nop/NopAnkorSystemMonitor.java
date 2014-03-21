package at.irian.ankor.monitor.nop;

import at.irian.ankor.monitor.AnkorSystemMonitor;
import at.irian.ankor.monitor.ModelSessionMonitor;
import at.irian.ankor.monitor.SwitchboardMonitor;

/**
 * @author Manfred Geiler
 */
public class NopAnkorSystemMonitor implements AnkorSystemMonitor {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NopAnkorSystemMonitor.class);

    private final SwitchboardMonitor switchboardMonitor = new NopSwitchboardMonitor();
    private final ModelSessionMonitor modelSessionMonitor = new NopModelSessionMonitor();

    @Override
    public SwitchboardMonitor switchboard() {
        return switchboardMonitor;
    }

    @Override
    public ModelSessionMonitor modelSession() {
        return modelSessionMonitor;
    }

}
