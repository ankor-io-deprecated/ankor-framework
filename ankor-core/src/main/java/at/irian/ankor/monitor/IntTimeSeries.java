package at.irian.ankor.monitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Thomas Spiegl
 */
public class IntTimeSeries {

    private IntTimeRange latest;
    private final long interval;
    private final List<IntTimeRange> values;

    protected IntTimeSeries(long interval) {
        this.interval = interval;
        this.values = new ArrayList<IntTimeRange>();
    }

    void increment() {
        setLatest();
        latest.increment();
    }

    void decrement() {
        setLatest();
        latest.decrement();
    }

    private void setLatest() {
        final long now = System.currentTimeMillis();
        if (latest == null) {
            latest = new IntTimeRange(now, now + interval);
            values.add(latest);
        } else {
            if (!latest.isValid(now)) {
                latest = this.latest.next(now, interval);
                values.add(latest);
            }
        }
    }

    public void resetTo(long time) {
        for (Iterator<IntTimeRange> it =  values.iterator(); it.hasNext(); ) {
            if (it.next().getEnd() < time) {
                it.remove();
            }
        }
    }

    public int getTotalValue() {
        return latest != null ? latest.getTotalValue() : 0;
    }

    public int getValue() {
        return latest != null ? latest.getValue() : 0;
    }
}
