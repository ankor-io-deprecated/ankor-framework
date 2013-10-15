package at.irian.ankor.bigcoll;

import at.irian.ankor.action.Action;
import at.irian.ankor.action.ActionEvent;
import at.irian.ankor.action.ActionEventListener;
import at.irian.ankor.action.RemoteAction;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public class MissingItemActionEventListener extends ActionEventListener {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MissingItemActionEventListener.class);

    public static final String ACTION_NAME = "@MissingItem";
    public static final String INDEX_PARAM = "index";

    public MissingItemActionEventListener() {
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
            Number index = (Number)action.getParams().get(INDEX_PARAM);
            Ref missingProperty = event.getActionProperty().appendIndex(index.intValue());
            LOG.debug("Sending requested missing property {}", missingProperty);
            missingProperty.signalValueChange();
        }
    }
}
