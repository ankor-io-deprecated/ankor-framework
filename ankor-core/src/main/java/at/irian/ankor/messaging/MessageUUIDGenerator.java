package at.irian.ankor.messaging;

import java.util.UUID;

/**
 * @author Manfred Geiler
 */
class MessageUUIDGenerator {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageUUIDGenerator.class);

    public String createId() {
        return UUID.randomUUID().toString();
    }

}
