package at.irian.ankor.big.modify;

import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.modify.AbstractModifier;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ClientSideBigListModifier extends AbstractModifier implements Modifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSideBigListModifier.class);

    private final SimpleTreeBigListChangeModifier bigListModifier;

    public ClientSideBigListModifier(Modifier parent) {
        super(parent);
        this.bigListModifier = new SimpleTreeBigListChangeModifier();
    }

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        // todo:  modify for sending BigList instances back to server
        return super.modifyBeforeSend(change, changedProperty);
    }

    @Override
    public Change modifyAfterReceive(Change change, Ref changedProperty) {
        change = bigListModifier.modify(change, changedProperty);
        return super.modifyAfterReceive(change, changedProperty);
    }

}
