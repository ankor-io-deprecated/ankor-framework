package at.irian.ankor.event.dispatch;

import at.irian.ankor.event.ModelEvent;
import javafx.application.Platform;

/**
 * @author Manfred Geiler
 */
public class JavaFxEventDispatcher implements EventDispatcher {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(JavaFxEventDispatcher.class);

    private final EventDispatcher delegateEventDispatcher;

    public JavaFxEventDispatcher(EventDispatcher delegateEventDispatcher) {
        this.delegateEventDispatcher = delegateEventDispatcher;
    }

    @Override
    public void dispatch(final ModelEvent event) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                delegateEventDispatcher.dispatch(event);
            }
        });
    }

    @Override
    public void close() {
        delegateEventDispatcher.close();
    }
}
