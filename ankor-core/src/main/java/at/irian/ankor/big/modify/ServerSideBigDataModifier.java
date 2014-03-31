package at.irian.ankor.big.modify;

import at.irian.ankor.change.Change;
import at.irian.ankor.serialization.modify.AbstractModifier;
import at.irian.ankor.serialization.modify.Modifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ServerSideBigDataModifier extends AbstractModifier implements Modifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSideBigDataModifier.class);

    private final BigDataChangeModifier bigDataModifier;

    public ServerSideBigDataModifier(Modifier parent) {
        super(parent);
        this.bigDataModifier = new BigDataChangeModifier();
    }

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        change = bigDataModifier.modify(change, changedProperty);
        return super.modifyBeforeSend(change, changedProperty);
    }

}
