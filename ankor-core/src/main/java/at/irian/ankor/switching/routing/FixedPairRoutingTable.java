package at.irian.ankor.switching.routing;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A RoutingTable that statically connects two addresses.
 *
 * @author Manfred Geiler
 */
public class FixedPairRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedPairRoutingTable.class);

    private ModelAddress a = null;
    private ModelAddress b = null;

    @Override
    public boolean connect(ModelAddress a, ModelAddress b) {
        this.a = a;
        this.b = b;
        return true;
    }

    @Override
    public boolean disconnect(ModelAddress a, ModelAddress b) {
        if (a.equals(this.a) && b.equals(this.b)) {
            this.a = null;
            this.b = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isConnected(ModelAddress a, ModelAddress b) {
        return a.equals(this.a) && b.equals(this.b) || a.equals(this.b) && b.equals(this.a);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean disconnectAll(ModelAddress p) {
        if (p.equals(this.a)) {
            return disconnect(p, this.b);
        } else if (p.equals(this.b)) {
                return disconnect(p, this.a);
        } else {
            return false;
        }
    }

    @Override
    public Collection<ModelAddress> getConnectedAddresses(ModelAddress p) {
        if (p.equals(this.a)) {
            return Collections.singleton(this.b);
        } else if (p.equals(this.b)) {
            return Collections.singleton(this.a);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasConnectedAddresses(ModelAddress p) {
        return p.equals(this.a) || p.equals(this.b);
    }

    @Override
    public Collection<ModelAddress> getAllConnectedAddresses() {
        return this.a != null && this.b != null
               ? new HashSet<ModelAddress>(Arrays.asList(a, b))
               : Collections.<ModelAddress>emptySet();
    }

    @Override
    public void clear() {
        this.a = null;
        this.b = null;
    }
}
