package at.irian.ankor.core.server;

import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.ModelRef;

/**
 * @author MGeiler (Manfred Geiler)
 */
public abstract class AnkorServerBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerBase.class);

    protected final Application application;
    protected final RemoteActionHandler remoteActionHandler;
    protected final RemoteChangeHandler remoteChangeHandler;
    private LocalListener localListener = null;

    public AnkorServerBase(Application application) {
        this.application = application;
        this.remoteChangeHandler = new RemoteChangeHandler(application.getListenerRegistry());
        this.remoteActionHandler = new RemoteActionHandler(application.getListenerRegistry());
    }

    public void init() {
        LOG.debug("Init {}", this);
        if (localListener != null) {
            throw new IllegalStateException("already initialized");
        }
        localListener = new LocalListener();
        application.getListenerRegistry().registerLocalChangeListener(null, localListener);
        application.getListenerRegistry().registerLocalActionListener(null, localListener);
    }

    public void close() {
        LOG.debug("Close {}", this);
        if (localListener != null) {
            application.getListenerRegistry().unregisterListener(localListener);
            localListener = null;
        }
    }


    protected void handleRemoteAction(String path, String action) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        handleRemoteAction(modelRef, action);
    }

    protected void handleRemoteAction(ModelRef modelRef, String action) {
        remoteActionHandler.handleRemoteAction(modelRef, action);
    }

    protected void handleRemoteChange(String path, Object newValue) {
        ModelRef modelRef = application.getRefFactory().ref(path);
        handleRemoteChange(modelRef, newValue);
    }

    protected void handleRemoteChange(ModelRef modelRef, Object newValue) {
        remoteChangeHandler.handleRemoteChange(modelRef, newValue);
    }

    protected abstract void handleLocalChange(ModelRef modelRef, Object oldValue, Object newValue);

    protected abstract void handleLocalAction(ModelRef modelRef, String action);




    private class LocalListener implements ModelActionListener, ModelChangeListener {
        @Override
        public void handleModelAction(ModelRef modelRef, String action) {
            LOG.debug("Local action detected by {} - {}: {}", this, modelRef, action);
            handleLocalAction(modelRef, action);
        }

        @Override
        public void beforeModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
        }

        @Override
        public void afterModelChange(ModelRef modelRef, Object oldValue, Object newValue) {
            LOG.debug("Local model change detected by {} - {}: {} => {}", this, modelRef, oldValue, newValue);
            handleLocalChange(modelRef, oldValue, newValue);
        }
    }

}
