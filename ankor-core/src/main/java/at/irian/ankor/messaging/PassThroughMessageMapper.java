package at.irian.ankor.messaging;

/**
 * @author Manfred Geiler
 */
@Deprecated
public class PassThroughMessageMapper implements MessageMapper<Message> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PassThroughMessageMapper.class);

    @Override
    public Message serialize(Object msg) {
        return (Message)msg;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <M> M deserialize(Message msg, Class<M> type) {
        return (M)msg;
    }
}
