package at.irian.ankor.switching.routing;

import java.util.*;

/**
 * A simple full-functional RoutingTable that is NOT thread-safe.
 *
 * @author Manfred Geiler
 */
public class SimpleRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RoutingTable.class);

    private final Map<ModelAddress, Collection<ModelAddress>> connections = new HashMap<ModelAddress, Collection<ModelAddress>>();

    @Override
    public boolean connect(ModelAddress a, ModelAddress b) {
        if (a == null) {
            throw new NullPointerException("ModelAddress a");
        }
        if (b == null) {
            throw new NullPointerException("ModelAddress b");
        }

        if (!isConnected(a, b)) {
            _connectOneWay(a, b);
            _connectOneWay(b, a);
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
            _disconnectOneWay(a, b);
            _disconnectOneWay(b, a);
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

        return _isConnectedOneWay(a, b) || _isConnectedOneWay(b, a);
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
    }


    @Override
    public Collection<ModelAddress> getConnectedAddresses(ModelAddress modelAddress) {
        if (modelAddress == null) {
            throw new NullPointerException("modelAddress");
        }

        Collection<ModelAddress> addresses = connections.get(modelAddress);
        return addresses != null ? Collections.unmodifiableCollection(addresses) : Collections.<ModelAddress>emptySet();
    }

    @Override
    public boolean hasConnectedAddresses(ModelAddress modelAddress) {
        if (modelAddress == null) {
            throw new NullPointerException("modelAddress");
        }

        Collection<ModelAddress> addresses = connections.get(modelAddress);
        return addresses != null && addresses.size() > 0;
    }

    @Override
    public Collection<ModelAddress> getAllConnectedAddresses() {
        Collection<ModelAddress> addresses = connections.keySet();
        return Collections.unmodifiableCollection(addresses);
    }

    @Override
    public void clear() {
        connections.clear();
    }
}
