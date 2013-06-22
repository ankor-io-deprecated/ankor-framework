package at.irian.ankor.messaging;

import java.util.UUID;

/**
 * @author Manfred Geiler
 */
public class MessageIdFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageIdFactory.class);

    public String createId() {
        return UUID.randomUUID().toString();
    }

}
