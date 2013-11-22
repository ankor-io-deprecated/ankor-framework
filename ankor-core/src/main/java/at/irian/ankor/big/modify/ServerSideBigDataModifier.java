package at.irian.ankor.big.modify;

import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.modify.AbstractModifier;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ServerSideBigDataModifier extends AbstractModifier implements Modifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSideBigDataModifier.class);

    private final AnnotationAwareBigDataChangeModifier bigListModifier;

    public ServerSideBigDataModifier(Modifier parent) {
        super(parent);
        this.bigListModifier = new AnnotationAwareBigDataChangeModifier();
    }

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        change = bigListModifier.modify(change, changedProperty);
        return super.modifyBeforeSend(change, changedProperty);
    }

}
