package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
@Deprecated
public class CounterMessageIdGenerator implements MessageIdGenerator{
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UuidMessageIdGenerator.class);

    private final String prefix;
    private int cnt = 0;

    public CounterMessageIdGenerator(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String create() {
        return prefix + (++cnt);
    }

}
