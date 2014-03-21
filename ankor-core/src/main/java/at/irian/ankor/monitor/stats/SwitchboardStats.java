package at.irian.ankor.monitor.stats;

/**
 * @author Manfred Geiler
 */
public class SwitchboardStats {
     //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SwitchboardStats.class);

    private volatile int totalInboundMessages = 0;
    private volatile int totalOutboundMessages = 0;

    public void incrementInboundMessages(int n) {
        totalInboundMessages += n;
    }

    public void incrementOutboundMessages(int n) {
        totalOutboundMessages += n;
    }

    public int getTotalInboundMessages() {
        return totalInboundMessages;
    }

    public int getTotalOutboundMessages() {
        return totalOutboundMessages;
    }
}
