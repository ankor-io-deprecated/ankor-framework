package at.irian.ankor.switching.routing;

import at.irian.ankor.switching.party.Party;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

/**
 * A RoutingTable that statically connects two parties.
 *
 * @author Manfred Geiler
 */
public class FixedPairRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedPairRoutingTable.class);

    private Party a = null;
    private Party b = null;

    @Override
    public boolean connect(Party a, Party b) {
        this.a = a;
        this.b = b;
        return true;
    }

    @Override
    public boolean disconnect(Party a, Party b) {
        if (a.equals(this.a) && b.equals(this.b)) {
            this.a = null;
            this.b = null;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isConnected(Party a, Party b) {
        return a.equals(this.a) && b.equals(this.b) || a.equals(this.b) && b.equals(this.a);
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean disconnectAll(Party p) {
        if (p.equals(this.a)) {
            return disconnect(p, this.b);
        } else if (p.equals(this.b)) {
                return disconnect(p, this.a);
        } else {
            return false;
        }
    }

    @Override
    public Collection<Party> getConnectedParties(Party p) {
        if (p.equals(this.a)) {
            return Collections.singleton(this.b);
        } else if (p.equals(this.b)) {
            return Collections.singleton(this.a);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasConnectedParties(Party p) {
        return p.equals(this.a) || p.equals(this.b);
    }

    @Override
    public Collection<Party> getAllConnectedParties() {
        return this.a != null && this.b != null
               ? new HashSet<Party>(Arrays.asList(a, b))
               : Collections.<Party>emptySet();
    }

    @Override
    public void clear() {
        this.a = null;
        this.b = null;
    }
}
