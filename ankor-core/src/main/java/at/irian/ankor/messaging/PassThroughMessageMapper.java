package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
public class PassThroughMessageMapper implements MessageMapper<Message> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PassThroughMessageMapper.class);

    @Override
    public Message serialize(Message msg) {
        return msg;
    }

    @Override
    public Message deserialize(Message msg) {
        return msg;
    }
}
