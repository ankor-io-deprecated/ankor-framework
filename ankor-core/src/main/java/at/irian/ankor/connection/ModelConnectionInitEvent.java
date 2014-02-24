package at.irian.ankor.connection;

import at.irian.ankor.event.ModelEvent;
import at.irian.ankor.event.ModelEventListener;
import at.irian.ankor.event.source.Source;

/**
 * @author Manfred Geiler
 */
public class ModelConnectionInitEvent extends ModelEvent {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ModelConnectionInitEvent.class);

    private final ModelConnection modelConnection;

    public ModelConnectionInitEvent(Source source, ModelConnection modelConnection) {
        super(source);
        this.modelConnection = modelConnection;
    }

    public ModelConnection getModelConnection() {
        return modelConnection;
    }

    @Override
    public boolean isAppropriateListener(ModelEventListener listener) {
        return listener instanceof Listener;
    }

    @Override
    public void processBy(ModelEventListener listener) {
        ((Listener)listener).processModelConnectionInit(this);
    }

    public interface Listener {
        void processModelConnectionInit(ModelConnectionInitEvent modelConnectionInitEvent);
    }
}
