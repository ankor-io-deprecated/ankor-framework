package at.irian.ankor.event;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.big.BigListMetadata;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.ModelSource;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;
import at.irian.ankor.viewmodel.metadata.MetadataUtils;
import at.irian.ankor.viewmodel.metadata.PropertyMetadata;

import java.util.List;

/**
 * Special use case action listener that listens for "missing property" actions and reacts by firing a change event
 * on the corresponding property ref.
 *
 * @author Manfred Geiler
 * @see at.irian.ankor.big.MissingPropertyActionFiringBigList
 */
public class MissingPropertyActionEventListener extends ActionEventListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingPropertyActionEventListener.class);

    public static final String MISSING_PROPERTY_ACTION_NAME = "@missingProperty";

    public MissingPropertyActionEventListener() {
        super(null); //global listener
    }

    @Override
    public boolean isDiscardable() {
        return false;
    }

    @Override
    public void process(ActionEvent event) {
        Action action = event.getAction();
        if (!event.isLocalEvent() && MISSING_PROPERTY_ACTION_NAME.equals(action.getName())) {
            Ref missingProperty = event.getActionProperty();
            LOG.debug("handling missing property request for {}", missingProperty);

            // special AnkorBigList handling
            if (!missingProperty.isRoot()) {
                Ref maybeCollRef = missingProperty.parent();
                PropertyMetadata propertyMetadata = MetadataUtils.getMetadataFor(maybeCollRef);
                BigListMetadata bigListMetadata = propertyMetadata.getGenericMetadata(BigListMetadata.class);
                if (bigListMetadata != null) {
                    int chunkSize = bigListMetadata.getChunkSize();
                    if (chunkSize > 1) {
                        int fromIndex = Integer.parseInt(missingProperty.propertyName());
                        List list = maybeCollRef.getValue();
                        int toIndex = Math.min(fromIndex + chunkSize, list.size());
                        if (fromIndex <= toIndex) {
                            List subList = list.subList(fromIndex, toIndex);
                            Change change = Change.replaceChange(fromIndex, subList);
                            ((RefImplementor)maybeCollRef).signal(ModelSource.createFrom(missingProperty, this), change);
                        }
                        return;
                    }
                }
            }

            // default behaviour
            missingProperty.signalValueChange();
        }
    }
}
