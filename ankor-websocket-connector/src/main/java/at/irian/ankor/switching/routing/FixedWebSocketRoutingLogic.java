package at.irian.ankor.switching.routing;

import at.irian.ankor.switching.connector.websocket.WebSocketModelAddress;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public class FixedWebSocketRoutingLogic implements RoutingLogic {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedSocketRoutingLogic.class);

    private final String clientId;

    public FixedWebSocketRoutingLogic(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public ModelAddress findRoutee(ModelAddress sender, Map<String, Object> connectParameters) {
        return new WebSocketModelAddress(clientId, sender.getModelName());
    }
}
