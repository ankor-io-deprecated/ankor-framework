package at.irian.ankor.event.source;

import at.irian.ankor.event.source.Source;
import at.irian.ankor.msg.party.Party;

/**
 * Event source for an event that is derived from an incoming message from a remote party.
 * A PartySource is always associated to a corresponding Party.
 *
 * @author Manfred Geiler
 */
public class PartySource implements Source {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PartySource.class);

    private final Party party;

    public PartySource(Party party) {
        this.party = party;
    }

    public Party getParty() {
        return party;
    }
}
