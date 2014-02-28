package at.irian.ankor.event.dispatch;

import at.irian.ankor.session.ModelSession;
import at.irian.ankor.event.ModelEvent;
import javafx.application.Platform;

/**
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcher extends SynchronisedEventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaFxEventDispatcher.class);

    public JavaFxEventDispatcher(ModelSession modelSession) {
        super(modelSession);
    }

    @Override
    public void dispatch(final ModelEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                JavaFxEventDispatcher.super.dispatch(event);
            }
        });
    }
}
