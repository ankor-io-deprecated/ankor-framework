package at.irian.ankor.msg;

import at.irian.ankor.msg.party.Party;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Manfred Geiler
 */
public class SwitchingCenter {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SwitchingCenter.class);

    private final Map<Party, Collection<Party>> connections = new HashMap<Party, Collection<Party>>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    public void connect(Party a, Party b) {
        connectOneWay(a, b);
        connectOneWay(b, a);
    }

    private void connectOneWay(Party a, Party b) {
        Lock lock = rwl.readLock();
        try {
            Collection<Party> parties = connections.get(a);
            if (parties == null || !parties.contains(b)) {
                lock.unlock();
                lock = rwl.writeLock();
                if (parties == null) {
                    parties = new HashSet<Party>();
                } else {
                    parties = new HashSet<Party>(parties);
                }
                parties.add(b);
                connections.put(a, parties);
            }
        } finally {
            lock.unlock();
        }
    }

    public void disconnect(Party a, Party b) {
        disconnectOneWay(a, b);
        disconnectOneWay(b, a);
    }

    private void disconnectOneWay(Party a, Party b) {
        Lock lock = rwl.readLock();
        try {
            Collection<Party> parties = connections.get(a);
            if (parties != null && parties.contains(b)) {
                lock.unlock();
                lock = rwl.writeLock();
                if (parties.size() == 1) {
                    connections.remove(a);
                } else {
                    parties = new HashSet<Party>(parties);
                    parties.remove(b);
                    connections.put(a, parties);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean isConnected(Party a, Party b) {
        Lock lock = rwl.readLock();
        try {
            Collection<Party> parties = connections.get(a);
            if (parties != null && parties.contains(b)) {
                return true;
            }
            parties = connections.get(b);
            return parties != null && parties.contains(a);
        } finally {
            lock.unlock();
        }
    }

    public void disconnectAll(Party a) {
        Lock lock = rwl.readLock();
        try {
            Collection<Party> parties = connections.get(a);
            if (parties != null) {
                lock.unlock();
                lock = rwl.writeLock();
                for (Party b : parties) {
                    disconnectOneWayWithoutLock(b, a);
                }
                connections.remove(a);
            }
        } finally {
            lock.unlock();
        }
    }

    private void disconnectOneWayWithoutLock(Party a, Party b) {
        Collection<Party> parties = connections.get(a);
        if (parties != null && parties.contains(b)) {
            if (parties.size() == 1) {
                connections.remove(a);
            } else {
                parties = new HashSet<Party>(parties);
                parties.remove(b);
                connections.put(a, parties);
            }
        }
    }

    public Collection<Party> getConnectedParties(Party party) {
        Lock lock = rwl.readLock();
        try {
            return connections.get(party);
        } finally {
            lock.unlock();
        }
    }

    public boolean hasConnectedParties(Party party) {
        Lock lock = rwl.readLock();
        try {
            Collection<Party> parties = connections.get(party);
            return parties != null && parties.size() > 0;
        } finally {
            lock.unlock();
        }
    }

}
