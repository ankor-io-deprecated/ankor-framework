package at.irian.ankor.core.server;

import at.irian.ankor.core.action.ModelAction;
import at.irian.ankor.core.application.Application;
import at.irian.ankor.core.listener.*;
import at.irian.ankor.core.ref.Ref;

import java.util.Collection;

/**
 * @author MGeiler (Manfred Geiler)
 */
public abstract class AnkorServerBase {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AnkorServerBase.class);

    protected final Application application;

    public AnkorServerBase(Application application) {
        this.application = application;
    }

    public void start() {
        LOG.debug("Starting {}", this);

        ListenerRegistry listenerRegistry = application.getListenerRegistry();

        listenerRegistry.registerLocalChangeListener(null, new LocalChangeListener());
        listenerRegistry.registerLocalActionListener(null, new LocalActionListener());

        AutoUnregisterChangeListener autoUnregisterChangeListener = new AutoUnregisterChangeListener(listenerRegistry);
        listenerRegistry.registerLocalChangeListener(null, autoUnregisterChangeListener);
        listenerRegistry.registerRemoteChangeListener(null, autoUnregisterChangeListener);
  }

    public void stop() {
        LOG.debug("Stopping {}", this);
        application.getListenerRegistry().unregisterAllListeners();
    }


    protected Application getApplication() {
        return application;
    }

    protected void receiveAction(Ref contextRef, ModelAction action) {
        LOG.debug("Remote action received by {} - {}: {}", AnkorServerBase.this, contextRef, action);

        // notify action listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<ActionListener> listeners = listenerRegistry.getRemoteActionListenersFor(contextRef);
        for (ActionListener listener : listeners) {
            listener.processAction(contextRef, action);
        }
    }

    protected void receiveChange(Ref modelContext, Ref changedRef, Object newValue) {
        LOG.debug("Remote change received by {} - {}: {} (context={})", AnkorServerBase.this, changedRef, newValue,
                  modelContext);

        if (modelContext != null) {
            changedRef = changedRef.withModelContext(modelContext);
        }

        // do change model (without notifying local listeners!)
        changedRef.unwatched().setValue(newValue);

        // notify change listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<BoundChangeListener> listenerInstances = listenerRegistry.getRemoteChangeListenersFor(changedRef);
        for (BoundChangeListener listenerInstance : listenerInstances) {
            ChangeListener listener = listenerInstance.getListener();
            Ref watchedRef = listenerInstance.getRef();
            listener.processChange(modelContext, watchedRef, changedRef);
        }
    }




    private class LocalActionListener implements ActionListener {
        @Override
        public void processAction(Ref actionContext, ModelAction action) {
            LOG.debug("Local action detected by {} - {}: {}", AnkorServerBase.this, actionContext, action);
            sendAction(actionContext, action);
        }
    }

    protected abstract void sendAction(Ref contextRef, ModelAction action);


    private class LocalChangeListener implements ChangeListener {
        @Override
        public void processChange(Ref contextRef, Ref watchedRef, Ref changedRef) {
            Object newValue = changedRef.getValue();
            LOG.debug("Local change detected by {} - {} => {}", AnkorServerBase.this, changedRef, newValue);
            sendChange(contextRef, changedRef, newValue);
        }
    }

    protected abstract void sendChange(Ref contextRef, Ref changedRef, Object newValue);


}
