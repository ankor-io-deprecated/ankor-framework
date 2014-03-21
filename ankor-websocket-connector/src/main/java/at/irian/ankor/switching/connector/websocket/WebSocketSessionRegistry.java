package at.irian.ankor.switching.connector.websocket;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import javax.websocket.Session;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Thomas Spiegl
 */
class WebSocketSessionRegistry {

    private volatile ImmutableSetMultimap<String, Session> sessions = ImmutableSetMultimap.of();

    private Lock writeLock = new ReentrantLock();

    public void addSession(String clientId, Session session) {
        writeLock.lock();
        try {
            sessions = ImmutableSetMultimap.<String, Session>builder()
                    .putAll(sessions)
                    .put(clientId, session)
                    .build();
        } finally {
            writeLock.unlock();
        }
    }

    public Set<Session> getSessions(String clientId) {
        return sessions.get(clientId);
    }

    public void removeSession(String clientId, Session session) {
        writeLock.lock();
        try {
            Multimap<String, Session> mutable = HashMultimap.create(sessions);
            mutable.remove(clientId, session);
            sessions = ImmutableSetMultimap.copyOf(mutable);
        } finally {
            writeLock.unlock();
        }
    }

    public boolean containsSession(String clientId, Session session) {
        return sessions.containsEntry(clientId, session);
    }
}
