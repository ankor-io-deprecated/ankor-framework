package at.irian.ankor.gateway.msg;

import at.irian.ankor.gateway.routing.RoutingTable;
import at.irian.ankor.gateway.party.Party;

import java.util.Collection;
import java.util.Collections;

/**
 * A RoutingTable that statically connects two parties.
 *
 * @author Manfred Geiler
 */
public class FixedPairRoutingTable implements RoutingTable {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(FixedPairRoutingTable.class);

    private Party a;
    private Party b;

    @Override
    public boolean connect(Party a, Party b) {
        this.a = a;
        this.b = b;
        return true;
    }

    @Override
    public boolean disconnect(Party a, Party b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isConnected(Party a, Party b) {
        return a.equals(this.a) && b.equals(this.b) || a.equals(this.b) && b.equals(this.a);
    }

    @Override
    public boolean disconnectAll(Party a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Party> getConnectedParties(Party party) {
        if (party.equals(a)) {
            return Collections.singleton(b);
        } else if (party.equals(b)) {
            return Collections.singleton(a);
        } else {
            return Collections.emptySet();
        }
    }

    @Override
    public boolean hasConnectedParties(Party party) {
        return party.equals(a) || party.equals(b);
    }
}
