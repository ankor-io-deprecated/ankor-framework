package at.irian.ankor.core.server;

import at.irian.ankor.core.action.MethodAction;
import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.ModelActionListener;
import at.irian.ankor.core.listener.ModelChangeListener;
import at.irian.ankor.core.ref.Ref;

/**
 * @author MGeiler (Manfred Geiler)
 */
public abstract class AnkorServerBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerBase.class);

    protected final Application application;
    protected final RemoteActionHandler remoteActionHandler;
    protected final RemoteChangeHandler remoteChangeHandler;
    private LocalListener localListener = null;
    private MethodActionListener methodActionListener = null;

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

        methodActionListener = new MethodActionListener();
        application.getListenerRegistry().registerRemoteActionListener(null, methodActionListener);
    }

    public void close() {
        LOG.debug("Close {}", this);
        if (localListener != null) {
            application.getListenerRegistry().unregisterListener(localListener);
            localListener = null;
        }
        if (methodActionListener != null) {
            application.getListenerRegistry().unregisterListener(methodActionListener);
            methodActionListener = null;
        }
    }


    protected void handleRemoteAction(String actionContextPath, ModelAction action) {
        handleRemoteAction(application.getRefFactory().ref(actionContextPath), action);
    }

    protected void handleRemoteAction(Ref actionContext, ModelAction action) {
        LOG.debug("Remote action received by {} - {}: {}", AnkorServerBase.this, actionContext, action);
        remoteActionHandler.handleRemoteAction(actionContext, action);
    }

    protected void handleRemoteChange(String propertyPath, Object newValue) {
        handleRemoteChange(application.getRefFactory().ref(propertyPath), newValue);
    }

    protected void handleRemoteChange(Ref ref, Object newValue) {
        LOG.debug("Remote change received by {} - {}: {}", AnkorServerBase.this, ref, newValue);
        remoteChangeHandler.handleRemoteChange(ref, newValue);
    }

    protected abstract void handleLocalChange(Ref ref, Object newValue);

    protected abstract void handleLocalAction(Ref ref, ModelAction action);




    private class LocalListener implements ModelActionListener, ModelChangeListener {
        @Override
        public void handleModelAction(Ref actionContext, ModelAction action) {
            LOG.debug("Local action detected by {} - {}: {}", AnkorServerBase.this, actionContext, action);
            handleLocalAction(actionContext, action);
        }

        @Override
        public void handleModelChange(Ref watchedRef, Ref changedRef) {
            Object newValue = changedRef.getValue();
            LOG.debug("Local model change detected by {} - {} => {}", AnkorServerBase.this, changedRef, newValue);
            handleLocalChange(changedRef, newValue);
        }
    }


    private class MethodActionListener implements ModelActionListener {
        @Override
        public void handleModelAction(Ref actionContext, ModelAction action) {
            if (action instanceof MethodAction) {
                LOG.debug("Remote method action detected by {} - {}: {}", AnkorServerBase.this, actionContext, action);
                MethodAction methodAction = (MethodAction) action;
                String methodExpression = methodAction.getMethodExpression();
                Object result = application.getMethodExecutor().execute(methodExpression, actionContext);
                Ref resultRef = methodAction.getResultRef(actionContext);
                if (resultRef != null) {
                    resultRef.setValue(result);
                } else {
                    application.getModelChangeWatcher().broadcastModelChange(actionContext);
                }
            }
        }
    }
}
