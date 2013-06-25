package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
class MessageUUIDGenerator {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MessageUUIDGenerator.class);

    private static int id = 0;

    public String createId() {
        //return UUID.randomUUID().toString();
        return "" + (id++);
    }

}
