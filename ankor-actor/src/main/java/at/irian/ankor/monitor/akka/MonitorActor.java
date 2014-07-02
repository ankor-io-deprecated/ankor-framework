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
    private final long logInterval;
    private final String logLevel;
    private Cancellable logStatsSchedule;

    public MonitorActor(AnkorSystemStats stats, long logInterval, String logLevel) {
        this.stats = stats;
        this.statsAnkorSystemMonitor = new StatsAnkorSystemMonitor(stats);
        this.logInterval = logInterval;
        this.logLevel = logLevel;
    }

    public static Props props(Config config, AnkorSystemStats stats) {
        long logInterval = config.getMilliseconds("at.irian.ankor.monitor.akka.MonitorActor.logInterval");
        String logLevel = config.getString("at.irian.ankor.monitor.akka.MonitorActor.logLevel");
        return Props.create(MonitorActor.class, stats, logInterval, logLevel);
    }

    @Override
    public void preStart() throws Exception {
        scheduleLogStats();
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        if (!logStatsSchedule.isCancelled()) {
            logStatsSchedule.cancel();
        }
        scheduleLogStats();
    }

    @Override
    public void postStop() {
        logStatsSchedule.cancel();
    }

    public static String name() {
        return "ankor_monitor";
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof MonitorMsg) {
            ((MonitorMsg) o).monitorTo(statsAnkorSystemMonitor);
        } else if ("logStats".equals(o)) {
            logStats();
        }
    }

    private void scheduleLogStats() {
        if (logInterval > 0) {
            this.logStatsSchedule = getContext().system().scheduler().schedule(
                    Duration.create(logInterval, TimeUnit.MILLISECONDS),
                    Duration.create(logInterval, TimeUnit.MILLISECONDS),
                    getSelf(), "logStats", getContext().dispatcher(), null);
        }
    }

    private void logStats() {
        if ("trace".equals(logLevel)) {
            LOG.trace(stats.toString());
        } else if ("debug".equals(logLevel)) {
            LOG.debug(stats.toString());
        } else if ("info".equals(logLevel)) {
            LOG.info(stats.toString());
        } else if ("warn".equals(logLevel)) {
            LOG.warn(stats.toString());
        } else if ("error".equals(logLevel)) {
            LOG.error(stats.toString());
        } else {
            throw new IllegalArgumentException("Unknown log level " + logLevel);
        }
    }

    public static interface MonitorMsg {
       void monitorTo(AnkorSystemMonitor monitor);
    }
}
