package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public class CounterMessageIdGenerator implements MessageIdGenerator{
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UuidMessageIdGenerator.class);

    private int cnt = 0;

    @Override
    public String create() {
        return "" + (++cnt);
    }

}
