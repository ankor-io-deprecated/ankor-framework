package at.irian.ankor.monitor;

/**
 * @author Thomas Spiegl
 */
public class IntTimeRange {

    private final long start;
    private final long end;
    private int initialValue;
    private int value;

    protected IntTimeRange(long start, long end) {
        this.start = start;
        this.end = end;
    }

    protected IntTimeRange(long start, long end, int initialValue) {
        this.start = start;
        this.end = end;
        this.initialValue = initialValue;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public int getValue() {
        return value;
    }

    @SuppressWarnings("UnusedDeclaration")
    public int getInitialValue() {
        return initialValue;
    }

    public int getTotalValue() {
        return initialValue + value;
    }

    public boolean isValid(long now) {
        return now <= end;
    }

    public void increment() {
        value++;
    }

    public void decrement() {
        value--;
    }

    public IntTimeRange next(long now, long interval) {
        if (now <= end) {
            throw new IllegalStateException("now <= end");
        }
        return new IntTimeRange(end + 1, now + interval, this.initialValue + this.value);
    }
}
