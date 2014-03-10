package at.irian.ankor.switching.routing;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Manfred Geiler
 */
public class ConcurrentRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RoutingTable.class);

    private final Map<ModelAddress, Collection<ModelAddress>> connections = new HashMap<ModelAddress, Collection<ModelAddress>>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    @Override
    public boolean connect(ModelAddress a, ModelAddress b) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }
        if (b == null) {
            throw new NullPointerException("ModelAddress b");
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

    private void _connectOneWay(ModelAddress a, ModelAddress b) {
        Collection<ModelAddress> addresses = connections.get(a);
        if (addresses == null) {
            addresses = new HashSet<ModelAddress>();
            addresses.add(b);
            connections.put(a, addresses);
        } else if (!addresses.contains(b)) {
            addresses = new HashSet<ModelAddress>(addresses);
            addresses.add(b);
            connections.put(a, addresses);
        }
    }


    @Override
    public boolean disconnect(ModelAddress a, ModelAddress b) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }
        if (b == null) {
            throw new NullPointerException("ModelAddress b");
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

    private void _disconnectOneWay(ModelAddress a, ModelAddress b) {
        Collection<ModelAddress> addresses = connections.get(a);
        if (addresses != null && addresses.contains(b)) {
            if (addresses.size() == 1) {
                connections.remove(a);
            } else {
                addresses = new HashSet<ModelAddress>(addresses);
                addresses.remove(b);
                connections.put(a, addresses);
            }
        }
    }

    @Override
    public boolean isConnected(ModelAddress a, ModelAddress b) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }
        if (b == null) {
            throw new NullPointerException("ModelAddress b");
        }

        rwl.readLock().lock();
        try {
            return _isConnectedOneWay(a, b) || _isConnectedOneWay(b, a);
        } finally {
            rwl.readLock().unlock();
        }
    }

    private boolean _isConnectedOneWay(ModelAddress a, ModelAddress b) {
        Collection<ModelAddress> addresses = connections.get(a);
        return addresses != null && addresses.contains(b);
    }

    @Override
    public boolean disconnectAll(ModelAddress a) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }

        rwl.writeLock().lock();
        try {
            Collection<ModelAddress> addresses = connections.get(a);
            if (addresses != null) {
                for (ModelAddress b : addresses) {
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
    public Collection<ModelAddress> getConnectedAddresses(ModelAddress modelAddress) {
        if (modelAddress == null) {
            throw new NullPointerException("modelAddress");
        }

        rwl.readLock().lock();
        try {
            Collection<ModelAddress> addresses = connections.get(modelAddress);
            return addresses != null ? Collections.unmodifiableCollection(addresses) : Collections.<ModelAddress>emptySet();
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public boolean hasConnectedAddresses(ModelAddress modelAddress) {
        if (modelAddress == null) {
            throw new NullPointerException("modelAddress");
        }

        rwl.readLock().lock();
        try {
            Collection<ModelAddress> addresses = connections.get(modelAddress);
            return addresses != null && addresses.size() > 0;
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public Collection<ModelAddress> getAllConnectedAddresses() {
        rwl.readLock().lock();
        try {
            Collection<ModelAddress> addresses = connections.keySet();
            return Collections.unmodifiableCollection(addresses);
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
