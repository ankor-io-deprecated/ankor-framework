package at.irian.ankor.msg;

/**
 * @author Manfred Geiler
 */
public class SimpleMessageBusFactory implements MessageBusFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleMessageBusFactory.class);

    @Override
    public MessageBus createMessageBus() {
        return new SimpleMessageBus();
    }
}
