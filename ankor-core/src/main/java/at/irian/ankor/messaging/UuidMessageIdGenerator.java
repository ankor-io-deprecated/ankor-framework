package at.irian.ankor.messaging;

import java.util.UUID;

/**
 * @author Manfred Geiler
 */
public class UuidMessageIdGenerator implements MessageIdGenerator{
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UuidMessageIdGenerator.class);

    @Override
    public String create() {
        return UUID.randomUUID().toString();
    }

}
