package at.irian.ankor.event.source;

import at.irian.ankor.switching.party.Party;

/**
 * Event source for an event that is derived from an incoming message from a remote party.
 * A PartySource is always associated to a corresponding Party.
 *
 * @author Manfred Geiler
 */
public class PartySource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PartySource.class);

    private final Party party;
    private final Object origination;

    public PartySource(Party party, Object origination) {
        this.party = party;
        this.origination = origination;
    }

    public Party getParty() {
        return party;
    }

    public Object getOrigination() {
        return origination;
    }

    @Override
    public String toString() {
        return "PartySource{" +
               "party=" + party +
               ", origination=" + origination +
               '}';
    }
}
