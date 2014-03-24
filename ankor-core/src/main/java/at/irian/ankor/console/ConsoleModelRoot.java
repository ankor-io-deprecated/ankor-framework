package at.irian.ankor.console;

import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.ref.Ref;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author Manfred Geiler
 */
@SuppressWarnings("UnusedDeclaration")
public class ConsoleModelRoot {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ConsoleModelRoot.class);

    private long totalInboundMessages = 0;
    private long totalOutboundMessages = 0;

    public ConsoleModelRoot(final Ref myRef, final AnkorSystemStats stats) {
        Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(@SuppressWarnings("NullableProblems") Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        }).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    totalInboundMessages = stats.switchboard().getTotalInboundMessages();
                    totalOutboundMessages = stats.switchboard().getTotalOutboundMessages();
                    myRef.signalValueChange();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public long getTotalInboundMessages() {
        return totalInboundMessages;
    }

    public void setTotalInboundMessages(long totalInboundMessages) {
        this.totalInboundMessages = totalInboundMessages;
    }

    public long getTotalOutboundMessages() {
        return totalOutboundMessages;
    }

    public void setTotalOutboundMessages(long totalOutboundMessages) {
        this.totalOutboundMessages = totalOutboundMessages;
    }
}
