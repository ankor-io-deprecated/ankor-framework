package at.irian.ankor.api.application;

import at.irian.ankor.api.event.ListenerRegistry;
import at.irian.ankor.api.lifecycle.Lifecycle;
import at.irian.ankor.api.model.ModelManager;
import at.irian.ankor.api.msgbus_deprecated.MessageBus;
import at.irian.ankor.api.protocol.ClientMessageDecoder;
import at.irian.ankor.api.protocol.ClientMessageEncoder;

import javax.el.ELContext;

/**
 */
public abstract class Application {

    public abstract ClientMessageDecoder getClientMessageDecoder();
    public abstract ClientMessageEncoder getClientMessageEncoder();
    public abstract StateManager getStateManager();
    public abstract ModelManager getModelManager();
    public abstract ListenerRegistry getListenerRegistry();
    public abstract ELContext getELContext();
    public abstract Lifecycle getLifecycle();

//    public abstract void registerListener(ModelPropertyPath modelPath, Object listener);
//    public abstract void unregisterListener(ModelPropertyPath modelPath, Object listener);
//    public abstract void unregisterAllListeners(ModelPropertyPath modelPath);

    // ---------------------

    private static Application instance;

    public static Application getInstance() {
        return instance;
    }

    public static void setInstance(Application instance) {
        Application.instance = instance;
    }
}
