package at.irian.ankor.switching.routing;

import at.irian.ankor.switching.party.Party;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Manfred Geiler
 */
public class ConcurrentRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RoutingTable.class);

    private final Map<Party, Collection<Party>> connections = new HashMap<Party, Collection<Party>>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    @Override
    public boolean connect(Party a, Party b) {
        if (a == null) {
            throw new NullPointerException("Party a");
        }
        if (b == null) {
            throw new NullPointerException("Party b");
        }

        if (!isConnected(a, b)) {
            rwl.writeLock().lock();
            try {
                _connectOneWay(a, b);
                _connectOneWay(b, a);
            } finally {
                rwl.writeLock().unlock();
            }
            return true;
        } else {
            return false;
        }
    }

    private void _connectOneWay(Party a, Party b) {
        Collection<Party> parties = connections.get(a);
        if (parties == null) {
            parties = new HashSet<Party>();
            parties.add(b);
            connections.put(a, parties);
        } else if (!parties.contains(b)) {
            parties = new HashSet<Party>(parties);
            parties.add(b);
            connections.put(a, parties);
        }
    }


    @Override
    public boolean disconnect(Party a, Party b) {
        if (a == null) {
            throw new NullPointerException("Party a");
        }
        if (b == null) {
            throw new NullPointerException("Party b");
        }

        if (isConnected(a, b)) {
            rwl.writeLock().lock();
            try {
                _disconnectOneWay(a, b);
                _disconnectOneWay(b, a);
            } finally {
                rwl.writeLock().unlock();
            }
            return true;
        } else {
            return false;
        }
    }

    private void _disconnectOneWay(Party a, Party b) {
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

    @Override
    public boolean isConnected(Party a, Party b) {
        if (a == null) {
            throw new NullPointerException("Party a");
        }
        if (b == null) {
            throw new NullPointerException("Party b");
        }

        rwl.readLock().lock();
        try {
            return _isConnectedOneWay(a, b) || _isConnectedOneWay(b, a);
        } finally {
            rwl.readLock().unlock();
        }
    }

    private boolean _isConnectedOneWay(Party a, Party b) {
        Collection<Party> parties = connections.get(a);
        return parties != null && parties.contains(b);
    }

    @Override
    public boolean disconnectAll(Party a) {
        if (a == null) {
            throw new NullPointerException("Party a");
        }

        rwl.writeLock().lock();
        try {
            Collection<Party> parties = connections.get(a);
            if (parties != null) {
                for (Party b : parties) {
                    _disconnectOneWay(b, a);
                }
                connections.remove(a);
                return true;
            } else {
                return false;
            }
        } finally {
            rwl.writeLock().unlock();
        }
    }


    @Override
    public Collection<Party> getConnectedParties(Party party) {
        if (party == null) {
            throw new NullPointerException("party");
        }

        rwl.readLock().lock();
        try {
            Collection<Party> parties = connections.get(party);
            return parties != null ? Collections.unmodifiableCollection(parties) : Collections.<Party>emptySet();
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public boolean hasConnectedParties(Party party) {
        if (party == null) {
            throw new NullPointerException("party");
        }

        rwl.readLock().lock();
        try {
            Collection<Party> parties = connections.get(party);
            return parties != null && parties.size() > 0;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public Collection<Party> getAllConnectedParties() {
        rwl.readLock().lock();
        try {
            Collection<Party> parties = connections.keySet();
            return Collections.unmodifiableCollection(parties);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        rwl.writeLock().lock();
        try {
            connections.clear();
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
