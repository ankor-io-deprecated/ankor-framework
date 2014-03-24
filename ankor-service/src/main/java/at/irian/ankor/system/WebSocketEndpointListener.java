package at.irian.ankor.system;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * @author Manfred Geiler
 */
public interface WebSocketEndpointListener {

    public void onOpen(Session session, EndpointConfig config);

    public void onClose(Session session, CloseReason closeReason);

    public void onError(Session session, Throwable thr);
}
