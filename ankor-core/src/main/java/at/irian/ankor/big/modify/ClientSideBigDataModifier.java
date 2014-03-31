package at.irian.ankor.big.modify;

import at.irian.ankor.change.Change;
import at.irian.ankor.serialization.modify.AbstractModifier;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ClientSideBigDataModifier extends AbstractModifier implements Modifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ClientSideBigDataModifier.class);

    private final SimpleTreeBigDataChangeModifier bigDataModifier;

    public ClientSideBigDataModifier(Modifier parent) {
        super(parent);
        this.bigDataModifier = new SimpleTreeBigDataChangeModifier();
    }

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        // todo:  modify for sending BigList instances back to server
        return super.modifyBeforeSend(change, changedProperty);
    }

    @Override
    public Change modifyAfterReceive(Change change, Ref changedProperty) {
        change = bigDataModifier.modify(change, changedProperty);
        return super.modifyAfterReceive(change, changedProperty);
    }

}
