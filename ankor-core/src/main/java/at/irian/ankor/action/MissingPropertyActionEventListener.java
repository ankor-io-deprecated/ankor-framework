package at.irian.ankor.action;

import at.irian.ankor.annotation.ModelPropertyAnnotationsFinder;
import at.irian.ankor.big.AnkorBigList;
import at.irian.ankor.change.Change;
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

    public static final String ACTION_NAME = "@missingProperty";

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
        if (action instanceof RemoteAction && ACTION_NAME.equals(action.getName())) {
            Ref missingProperty = event.getActionProperty();
            LOG.debug("handling missing property request for {}", missingProperty);

            if (!missingProperty.isRoot()) {
                Ref maybeCollRef = missingProperty.parent();
                AnkorBigList bigListAnnotation = new ModelPropertyAnnotationsFinder()
                        .findModelPropertyAnnotations(maybeCollRef, AnkorBigList.class);
                if (bigListAnnotation != null) {
                    int aheadSend = bigListAnnotation.aheadSendSize();
                    int fromIndex = Integer.parseInt(missingProperty.propertyName());
                    int toIndex = fromIndex + 1 + aheadSend;
                    if (aheadSend > 0) {
                        List list = maybeCollRef.getValue();
                        List subList = list.subList(fromIndex, Math.min(toIndex, list.size()));
                        Change change = Change.replaceChange(fromIndex, subList);
                        ((RefImplementor)maybeCollRef).signal(change);
                        return;
                    }
                }
            }

            missingProperty.signalValueChange();
        }
    }
}
