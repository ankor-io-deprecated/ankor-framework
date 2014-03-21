package at.irian.ankor.monitor.akka;

import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import at.irian.ankor.monitor.AnkorSystemMonitor;
import at.irian.ankor.monitor.stats.StatsAnkorSystemMonitor;
import at.irian.ankor.monitor.stats.AnkorSystemStats;
import com.typesafe.config.Config;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Spiegl
 */
public class MonitorActor extends UntypedActor {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MonitorActor.class);

    private final AnkorSystemStats stats;
    private final StatsAnkorSystemMonitor statsAnkorSystemMonitor;
    private final long logInitialDelay;
    private final long logInterval;
    private Cancellable logStats;

    public MonitorActor(long interval, long logInitialDelay, long logInterval) {
        this.stats = new AnkorSystemStats();
        this.statsAnkorSystemMonitor = new StatsAnkorSystemMonitor(stats);
        this.logInitialDelay = logInitialDelay;
        this.logInterval = logInterval;
    }

    public static Props props(@SuppressWarnings("UnusedParameters") Config config) {
        long interval = config.getMilliseconds("at.irian.ankor.monitor.akka.MonitorActor.interval");
        long logInitialDelay = config.getMilliseconds("at.irian.ankor.monitor.akka.MonitorActor.logInitialDelay");
        long logInterval = config.getMilliseconds("at.irian.ankor.monitor.akka.MonitorActor.logInterval");
        return Props.create(MonitorActor.class, interval, logInitialDelay, logInterval);
    }

    @Override
    public void preStart() throws Exception {
        scheduleLogStats();
    }

    private void scheduleLogStats() {
        this.logStats = getContext().system().scheduler().schedule(
                Duration.create(logInitialDelay, TimeUnit.MILLISECONDS),
                Duration.create(logInterval, TimeUnit.MILLISECONDS),
                getSelf(), "logStats", getContext().dispatcher(), null);
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        if (!logStats.isCancelled()) {
            logStats.cancel();
        }
        scheduleLogStats();
    }

    @Override
    public void postStop() {
        logStats.cancel();
    }

    public static String name() {
        return "ankor_monitor";
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof MonitorMsg) {
            ((MonitorMsg) o).monitorTo(statsAnkorSystemMonitor);
        } else if ("logStats".equals(o)) {
            LOG.info(stats.toString());
        }
    }


    public static interface MonitorMsg {
       void monitorTo(AnkorSystemMonitor monitor);
    }
}
