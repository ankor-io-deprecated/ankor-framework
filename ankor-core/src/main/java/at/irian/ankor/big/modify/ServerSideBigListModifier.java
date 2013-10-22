package at.irian.ankor.big.modify;

import at.irian.ankor.change.Change;
import at.irian.ankor.messaging.modify.AbstractModifier;
import at.irian.ankor.messaging.modify.Modifier;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class ServerSideBigListModifier extends AbstractModifier implements Modifier {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ServerSideBigListModifier.class);

    private final AnnotationAwareBigListChangeModifier bigListModifier;

    public ServerSideBigListModifier(Modifier parent) {
        super(parent);
        this.bigListModifier = new AnnotationAwareBigListChangeModifier();
    }

    @Override
    public Change modifyBeforeSend(Change change, Ref changedProperty) {
        change = bigListModifier.modify(change, changedProperty);
        return super.modifyBeforeSend(change, changedProperty);
    }

}
