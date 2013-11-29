package at.irian.ankor.ref;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.event.source.Source;

/**
 * Specialized action event that signals a missing view model property (i.e. an invalid Ref),
 *
 * @author Manfred Geiler
 */
public class MissingEvent extends ActionEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingEvent.class);

    public static final String ACTION_NAME = "propertyMissing";

    public MissingEvent(Source source, Ref actionProperty) {
        super(source, actionProperty, new Action(ACTION_NAME));
    }
}
