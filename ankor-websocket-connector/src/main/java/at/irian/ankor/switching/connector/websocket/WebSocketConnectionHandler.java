package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.switching.connector.ConnectionHandler;
import at.irian.ankor.switching.connector.HandlerScopeContext;
import at.irian.ankor.switching.routing.ModelAddress;

import java.util.Map;

/**
 * @author Thomas Spiegl
 */
public class WebSocketConnectionHandler implements ConnectionHandler<WebSocketModelAddress> {

    private final WebSocketSender webSocketSender;

    public WebSocketConnectionHandler(WebSocketSender webSocketSender) {
        this.webSocketSender = webSocketSender;
    }

    @Override
    public void openConnection(ModelAddress sender,
                               WebSocketModelAddress receiver,
                               Map<String, Object> connectParameters,
                               HandlerScopeContext context) {
        webSocketSender.send(sender,
                             receiver,
                             WebSocketMessage.createConnectMsg(sender.getModelName(), connectParameters),
                             context);
    }

    @Override
    public void closeConnection(ModelAddress sender,
                                WebSocketModelAddress receiver,
                                boolean lastRoute,
                                HandlerScopeContext context) {
        webSocketSender.send(sender,
                             receiver,
                             WebSocketMessage.createCloseMsg(sender.getModelName()),
                             context);
    }

}
