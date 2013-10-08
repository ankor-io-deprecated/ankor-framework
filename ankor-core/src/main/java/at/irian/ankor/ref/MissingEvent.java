package at.irian.ankor.ref;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;

/**
 * Specialized action event that signals a missing view model property (i.e. an invalid Ref),
 *
 * @author Manfred Geiler
 */
public class MissingEvent extends ActionEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingEvent.class);

    public static final String ACTION_NAME = "propertyMissing";

    public MissingEvent(Ref actionProperty) {
        super(actionProperty, new Action(ACTION_NAME));
    }
}
