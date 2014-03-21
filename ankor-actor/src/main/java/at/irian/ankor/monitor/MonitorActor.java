package at.irian.ankor.monitor;

import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.typesafe.config.Config;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeUnit;

/**
 * @author Thomas Spiegl
 */
public class MonitorActor extends UntypedActor {

    private final SimpleMonitor monitor;
    private final long logInitialDelay;
    private final long logInterval;
    private Cancellable logStats;

    public MonitorActor(long interval, long logInitialDelay, long logInterval) {
        monitor = new SimpleMonitor(interval);
        this.logInitialDelay = logInitialDelay;
        this.logInterval = logInterval;
    }

    public static Props props(@SuppressWarnings("UnusedParameters") Config config) {
        long interval = config.getMilliseconds("at.irian.ankor.monitor.MonitorActor.interval");
        long logInitialDelay = config.getMilliseconds("at.irian.ankor.monitor.MonitorActor.logInitialDelay");
        long logInterval = config.getMilliseconds("at.irian.ankor.monitor.MonitorActor.logInterval");
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
            ((MonitorMsg) o).writeTo(monitor);
        } else if ("logStats".equals(o)) {
            monitor.logStats();
        }
    }

    public static interface MonitorMsg {
        void writeTo(Monitor monitor);
    }
}
