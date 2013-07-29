package at.irian.ankor.session;

/**
 * @author Manfred Geiler
 */
public class CounterSessionIdGenerator implements SessionIdGenerator {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CounterSessionIdGenerator.class);

    private int cnt = 0;

    @Override
    public String create() {
        return "" + (++cnt);
    }
}
