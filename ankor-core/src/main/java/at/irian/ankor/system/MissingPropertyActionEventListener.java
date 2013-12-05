package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.annotation.ModelPropertyAnnotationsFinder;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.source.LocalSource;
import at.irian.ankor.ref.Ref;
import at.irian.ankor.ref.impl.RefImplementor;

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
        if (event.getSource() instanceof RemoteSource && MISSING_PROPERTY_ACTION_NAME.equals(action.getName())) {
            Ref missingProperty = event.getActionProperty();
            LOG.debug("handling missing property request for {}", missingProperty);

            // special AnkorBigList handling
            if (!missingProperty.isRoot()) {
                Ref maybeCollRef = missingProperty.parent();
                AnkorBigList bigListAnnotation = new ModelPropertyAnnotationsFinder()
                        .findModelPropertyAnnotations(maybeCollRef, AnkorBigList.class);
                if (bigListAnnotation != null) {
                    int chunkSize = bigListAnnotation.chunkSize();
                    if (chunkSize > 1) {
                        int fromIndex = Integer.parseInt(missingProperty.propertyName());
                        List list = maybeCollRef.getValue();
                        int toIndex = Math.min(fromIndex + chunkSize, list.size());
                        if (fromIndex <= toIndex) {
                            List subList = list.subList(fromIndex, toIndex);
                            Change change = Change.replaceChange(fromIndex, subList);
                            ((RefImplementor)maybeCollRef).signal(new LocalSource(missingProperty.context().modelContext()),
                                                                  change);
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
