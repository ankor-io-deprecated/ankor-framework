package at.irian.ankor.system;

import at.irian.ankor.action.Action;
import at.irian.ankor.change.Change;
import at.irian.ankor.event.ModelEventListener;

/**
 * @author Manfred Geiler
 */
public class RemoteEventListener implements ModelEventListener {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(RemoteEventListener.class);

    @Override
    public boolean isDiscardable() {
        return false;
    }

    public void process(RemoteEvent event) {

        //LOG.info("processing remote event {}", event);

        Action action = event.getAction();
        if (action != null) {
            event.getSourceProperty().fireAction(action);
        }

        Change change = event.getChange();
        if (change != null) {
            event.getSourceProperty().apply(change);
        }

    }

}
