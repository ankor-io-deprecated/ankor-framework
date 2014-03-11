package at.irian.ankor.switching.routing;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

/**
 * A thread-safe AND concurrent-modification-safe RoutingTable.
 * @author Manfred Geiler
 */
public class ConcurrentRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RoutingTable.class);

    private volatile ImmutableSetMultimap<ModelAddress, ModelAddress> connections = ImmutableSetMultimap.of();

    @Override
    public boolean connect(ModelAddress a, ModelAddress b) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }
        if (b == null) {
            throw new NullPointerException("ModelAddress b");
        }

        if (!isConnected(a, b)) {
            connections = ImmutableSetMultimap.<ModelAddress, ModelAddress>builder()
                                              .putAll(connections)
                                              .put(a, b)
                                              .put(b, a)
                                              .build();
            return true;
        } else {
            return false;
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
            Multimap<ModelAddress, ModelAddress> mutable = HashMultimap.create(connections);
            mutable.remove(a, b);
            mutable.remove(b, a);
            connections = ImmutableSetMultimap.copyOf(mutable);
            return true;
        } else {
            return false;
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

        return connections.containsEntry(a, b);
    }

    @Override
    public boolean disconnectAll(ModelAddress a) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }

        Collection<ModelAddress> addresses = connections.get(a);
        if (!addresses.isEmpty()) {
            for (ModelAddress b : addresses) {
                disconnect(a, b);
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public Collection<ModelAddress> getConnectedAddresses(ModelAddress modelAddress) {
        if (modelAddress == null) {
            throw new NullPointerException("modelAddress");
        }

        return connections.get(modelAddress);
    }

    @Override
    public boolean hasConnectedAddresses(ModelAddress modelAddress) {
        if (modelAddress == null) {
            throw new NullPointerException("modelAddress");
        }

        Collection<ModelAddress> addresses = connections.get(modelAddress);
        return !addresses.isEmpty();
    }

    @Override
    public Collection<ModelAddress> getAllConnectedAddresses() {
        return connections.keySet();
    }

    @Override
    public void clear() {
        connections = ImmutableSetMultimap.of();
    }
}
