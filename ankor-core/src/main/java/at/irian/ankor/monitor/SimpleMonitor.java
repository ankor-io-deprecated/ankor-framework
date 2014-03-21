package at.irian.ankor.monitor;

import at.irian.ankor.switching.msg.EventMessage;
import at.irian.ankor.switching.routing.ModelAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Thomas Spiegl
 */
@Deprecated
public class SimpleMonitor implements Monitor {

    private static Logger LOG = LoggerFactory.getLogger(SimpleMonitor.class);

    private long latestReset;
    private final SimpleMonitorStatistics stats;

    public SimpleMonitor(long period) {
        this.stats = new SimpleMonitorStatistics(period);
    }

    @Override
    public void connect(ModelAddress a, ModelAddress b) {
        reset();
        stats.getConnections().increment();
    }

    @Override
    public void disconnect(ModelAddress a, ModelAddress b) {
        reset();
        stats.getConnections().decrement();
    }

    @Override
    public void inboundMessage(ModelAddress sender) {
        reset();
        stats.getInbound(sender.getClass().getSimpleName()).increment();
    }

    @Override
    public void outboundMessage(ModelAddress receiver) {
        reset();
        stats.getOutbound(receiver.getClass().getSimpleName()).increment();
    }

    @Override
    public void send(ModelAddress sender, EventMessage message, ModelAddress receiver) {

    }

    @Override
    public void addModelSession() {
        reset();
        stats.getModelSessions().increment();
    }

    @Override
    public void removeModelSession() {
        reset();
        stats.getModelSessions().decrement();
    }

    public SimpleMonitorStatistics getStats() {
        return stats;
    }

    private void reset() {
        long now = System.currentTimeMillis();
        if (now > latestReset + 1000 * 10) {
            stats.resetTo(now - 6000000);
            latestReset = now;
        }
    }

    public void logStats() {
        LOG.error(getStats().toString()); // TODO info
    }
}
