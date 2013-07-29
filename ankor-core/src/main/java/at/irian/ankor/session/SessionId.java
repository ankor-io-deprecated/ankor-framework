package at.irian.ankor.session;

import java.util.UUID;

/**
 * @author Manfred Geiler
 */
public class SessionId {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SessionId.class);

    private final String id;

    SessionId(String id) {
        this.id = id;
    }

    public static SessionId create() {
        return new SessionId(UUID.randomUUID().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SessionId other = (SessionId) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
