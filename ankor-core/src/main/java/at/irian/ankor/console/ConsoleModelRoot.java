package at.irian.ankor.console;

import at.irian.ankor.monitor.stats.AnkorSystemStats;
import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;

import java.util.concurrent.Executors;
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
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    AnkorPatterns.runLater(myRef, new Runnable() {
                        @Override
                        public void run() {
                            myRef.appendPath("totalInboundMessages")
                                 .setValue(stats.switchboard().getTotalInboundMessages());
                            myRef.appendPath("totalOutboundMessages")
                                 .setValue(stats.switchboard().getTotalOutboundMessages());
                        }
                    });
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
