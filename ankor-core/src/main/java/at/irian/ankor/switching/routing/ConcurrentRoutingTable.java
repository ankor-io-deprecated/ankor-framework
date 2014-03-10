package at.irian.ankor.switching.routing;

import java.util.Collection;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe RoutingTable.
 *
 * @author Manfred Geiler
 */
public class ConcurrentRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RoutingTable.class);

    private final SimpleRoutingTable simpleRoutingTable = new SimpleRoutingTable();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();

    @Override
    public boolean connect(ModelAddress a, ModelAddress b) {
        rwl.writeLock().lock();
        try {
            return simpleRoutingTable.connect(a, b);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public boolean disconnect(ModelAddress a, ModelAddress b) {
        rwl.writeLock().lock();
        try {
            return simpleRoutingTable.disconnect(a, b);
        } finally {
            rwl.writeLock().unlock();
        }
    }

    @Override
    public boolean isConnected(ModelAddress a, ModelAddress b) {
        rwl.readLock().lock();
        try {
            return simpleRoutingTable.isConnected(a, b);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public boolean disconnectAll(ModelAddress a) {
        rwl.writeLock().lock();
        try {
            return simpleRoutingTable.disconnectAll(a);
        } finally {
            rwl.writeLock().unlock();
        }
    }


    @Override
    public Collection<ModelAddress> getConnectedAddresses(ModelAddress modelAddress) {
        rwl.readLock().lock();
        try {
            return simpleRoutingTable.getConnectedAddresses(modelAddress);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public boolean hasConnectedAddresses(ModelAddress modelAddress) {
        rwl.readLock().lock();
        try {
            return simpleRoutingTable.hasConnectedAddresses(modelAddress);
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public Collection<ModelAddress> getAllConnectedAddresses() {
        rwl.readLock().lock();
        try {
            return simpleRoutingTable.getAllConnectedAddresses();
        } finally {
            rwl.readLock().unlock();
        }
    }

    @Override
    public void clear() {
        rwl.writeLock().lock();
        try {
            simpleRoutingTable.clear();
        } finally {
            rwl.writeLock().unlock();
        }
    }
}
