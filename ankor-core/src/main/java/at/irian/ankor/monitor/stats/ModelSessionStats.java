package at.irian.ankor.monitor.stats;

/**
 * @author Manfred Geiler
 */
public class ModelSessionStats {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelSessionStats.class);

    private volatile int totalOpenSessions = 0;

    public void incrementOpenSessions() {
        totalOpenSessions++;
    }

    public void decrementOpenSessions() {
        totalOpenSessions--;
    }

    public int getTotalOpenSessions() {
        return totalOpenSessions;
    }
}
