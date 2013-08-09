package at.irian.ankor.fx.controller;

import at.irian.ankor.change.ChangeEvent;
import at.irian.ankor.change.ChangeEventListener;

/**
 * @author Thomas Spiegl
 */
public class FXControllerChangeListener extends ChangeEventListener {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ChangeListenerRegistry.class);

    public FXControllerChangeListener() {
        super(null);
    }

    @Override
    public void process(ChangeEvent event) {
        for (ChangeEventListener changeEventListener : FXControllerAnnotationSupport.annotationSupport().getChangeListeners()) {
            changeEventListener.process(event);
        }
    }
}
