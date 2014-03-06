package at.irian.ankor.gateway;

/**
 * @author Manfred Geiler
 */
public class SimpleGatewayFactory implements GatewayFactory {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleGatewayFactory.class);

    @Override
    public Gateway createGateway() {
        return new SimpleGateway();
    }
}
