package at.irian.ankor.switching.connector.websocket;

import at.irian.ankor.switching.connector.ConnectionHandler;
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
    public void openConnection(ModelAddress sender, WebSocketModelAddress receiver, Map<String, Object> connectParameters) {
        webSocketSender.send(
                sender,
                receiver,
                WebSocketMessage.createConnectMsg(null, sender.getModelName(), connectParameters));
    }

    @Override
    public void closeConnection(ModelAddress sender, WebSocketModelAddress receiver, boolean lastRoute) {
        webSocketSender.send(
                sender,
                receiver,
                WebSocketMessage.createCloseMsg(null, sender.getModelName()));
    }

}
