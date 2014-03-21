package at.irian.ankor.monitor.stats;

/**
 * @author Manfred Geiler
 */
public class SwitchboardStats {
     //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SwitchboardStats.class);

    private volatile long totalInboundMessages = 0;
    private volatile long totalOutboundMessages = 0;

    public void incrementInboundMessages() {
        totalInboundMessages++;
    }

    public void incrementOutboundMessages() {
        totalOutboundMessages++;
    }

    public long getTotalInboundMessages() {
        return totalInboundMessages;
    }

    public long getTotalOutboundMessages() {
        return totalOutboundMessages;
    }
}
