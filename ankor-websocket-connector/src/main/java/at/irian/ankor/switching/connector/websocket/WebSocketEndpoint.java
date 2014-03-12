package at.irian.ankor.switching.connector.websocket;


import at.irian.ankor.path.el.SimpleELPathSyntax;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.io.IOException;

/**
 * @author Thomas Spiegl
 */
public class WebSocketEndpoint extends Endpoint {

    private static Logger LOG = LoggerFactory.getLogger(WebSocketEndpoint.class);

    private static final String CLIENT_ID = WebSocketEndpoint.class.getName() + ".CLIENT_ID";

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        String clientId = getClientId(session);
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new IllegalStateException("No valid client id in path");
        }

        WebSocketConnector connector = WebSocketConnector.getInstance();

        Session oldSession = connector.getSessionRegistry().addSession(clientId, session);
        if (oldSession != null) {
            try {
                oldSession.close();
            } catch (IOException ignore) {
            }
        }

        WebSocketListener listener =
                new WebSocketListener(connector.getMessageMapper(), connector.getAnkorSystem().getSwitchboard(),
                        SimpleELPathSyntax.getInstance(), clientId);
        session.addMessageHandler(listener.getByteMessageHandler());
        session.addMessageHandler(listener.getStringMessageHandler());

        LOG.debug("New client connected {}", clientId);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        LOG.info("Invalidating session {} because of {}: {}", session.getId(), closeReason.getCloseCode(),
                closeReason.getReasonPhrase());
        unregisterSession(session);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        LOG.error("Invalidating session {} because of error {}", session.getId(), thr.getMessage());
        unregisterSession(session);
    }

    private void unregisterSession(Session session) {
        String clientId = getClientId(session);
        if (clientId != null) {
            WebSocketConnector.getInstance().getSessionRegistry().removeSession(clientId);
        }
    }

    private String getClientId(Session session) {
        String clientId = (String) session.getUserProperties().get(CLIENT_ID);
        if (clientId == null) {
            clientId = session.getPathParameters().get(CLIENT_ID);
            session.getUserProperties().put(CLIENT_ID, clientId);
        }
        return clientId;
    }
}
