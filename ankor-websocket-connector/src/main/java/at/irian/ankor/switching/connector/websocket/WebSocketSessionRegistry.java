package at.irian.ankor.switching.connector.websocket;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Spiegl
 */
class WebSocketSessionRegistry {

    private Map<String, Session> sessions = new ConcurrentHashMap<String, Session>(1000);

    public Session addSession(String clientId, Session session) {
        return sessions.put(clientId, session);
    }

    public Session getSession(String clientId) {
        return sessions.get(clientId);
    }

    public void removeSession(String clientId) {
        sessions.remove(clientId);
    }
}
