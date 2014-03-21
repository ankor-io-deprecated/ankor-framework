package at.irian.ankor.monitor.stats;

/**
 * @author Manfred Geiler
 */
public class AnkorSystemStats {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorSystemStats.class);

    private final SwitchboardStats switchboardStats = new SwitchboardStats();
    private final ModelSessionStats modelSessionStats = new ModelSessionStats();

    public SwitchboardStats switchboard() {
        return switchboardStats;
    }

    public ModelSessionStats modelSession() {
        return modelSessionStats;
    }

    @Override
    public String toString() {
        return String.format("Statistics: %d inbound messages, %d outbound messages",
                             switchboardStats.getTotalInboundMessages(),
                             switchboardStats.getTotalOutboundMessages());
    }
}
