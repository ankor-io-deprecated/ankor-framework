package at.irian.ankor.core.server;

import at.irian.ankor.core.action.CompleteAware;
import at.irian.ankor.core.action.MethodActionListener;
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

        // default action listeners
        MethodActionListener methodActionListener = new MethodActionListener(application);
        listenerRegistry.registerRemoteActionListener(null, methodActionListener);
    }

    public void stop() {
        LOG.debug("Stopping {}", this);
        application.getListenerRegistry().unregisterAllListeners();
    }


    protected void receiveAction(Ref actionContextRef, ModelAction action) {
        LOG.debug("Remote action received by {} - {}: {}", AnkorServerBase.this, actionContextRef, action);

        // notify listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<ModelActionListener> listeners = listenerRegistry.getRemoteActionListenersFor(actionContextRef);
        for (ModelActionListener listener : listeners) {
            listener.handleModelAction(actionContextRef, action);
        }

        //todo...
        if (action instanceof CompleteAware) {
            ((CompleteAware) action).complete(actionContextRef);
        }
    }

    protected void receiveChange(Ref changedRef, Object newValue) {
        LOG.debug("Remote change received by {} - {}: {}", AnkorServerBase.this, changedRef, newValue);

        // do change model (without notifying local listeners!)
        changedRef.unwatched().setValue(newValue);

        // notify listeners
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        Collection<ModelChangeListenerInstance> listenerInstances = listenerRegistry.getRemoteChangeListenersFor(changedRef);
        for (ModelChangeListenerInstance listenerInstance : listenerInstances) {
            ModelChangeListener listener = listenerInstance.getListener();
            Ref watchedRef = listenerInstance.getRef();
            listener.handleModelChange(watchedRef, changedRef);
        }
    }




    private class LocalActionListener implements ModelActionListener {
        @Override
        public void handleModelAction(Ref actionContextRef, ModelAction action) {
            LOG.debug("Local action detected by {} - {}: {}", AnkorServerBase.this, actionContextRef, action);
            sendAction(actionContextRef, action);
        }
    }

    protected abstract void sendAction(Ref actionContextRef, ModelAction action);


    private class LocalChangeListener implements ModelChangeListener {
        @Override
        public void handleModelChange(Ref watchedRef, Ref changedRef) {
            Object newValue = changedRef.getValue();
            LOG.debug("Local change detected by {} - {} => {}", AnkorServerBase.this, changedRef, newValue);
            sendChange(changedRef, newValue);
        }
    }

    protected abstract void sendChange(Ref changedRef, Object newValue);


}
