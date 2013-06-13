package at.irian.ankor.impl.application;

import at.irian.ankor.api.application.Application;
import at.irian.ankor.api.application.StateManager;
import at.irian.ankor.api.event.ListenerRegistry;
import at.irian.ankor.api.msgbus.MessageBus;
import at.irian.ankor.api.protocol.ClientMessageDecoder;
import at.irian.ankor.api.protocol.ClientMessageEncoder;
import at.irian.ankor.api.model.ModelManager;

import javax.el.ELContext;

/**
 */
public class DefaultApplication extends Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultApplication.class);

    private MessageBus messageBus;
    private ClientMessageDecoder clientMessageDecoder;
    private StateManager stateManager;
    private ModelManager modelManager;
    private ListenerRegistry listenerRegistry;
    private ClientMessageEncoder clientMessageEncoder;
    private ELContext elContext;

    public DefaultApplication(MessageBus messageBus,
                              ClientMessageDecoder clientMessageDecoder,
                              StateManager stateManager,
                              ModelManager modelManager,
                              ListenerRegistry listenerRegistry,
                              ClientMessageEncoder clientMessageEncoder,
                              ELContext elContext) {
        this.messageBus = messageBus;
        this.clientMessageDecoder = clientMessageDecoder;
        this.stateManager = stateManager;
        this.modelManager = modelManager;
        this.listenerRegistry = listenerRegistry;
        this.clientMessageEncoder = clientMessageEncoder;
        this.elContext = elContext;
    }

    public MessageBus getMessageBus() {
        return messageBus;
    }

    public void setMessageBus(MessageBus messageBus) {
        this.messageBus = messageBus;
    }

    public ClientMessageDecoder getClientMessageDecoder() {
        return clientMessageDecoder;
    }

    public void setClientMessageDecoder(ClientMessageDecoder clientMessageDecoder) {
        this.clientMessageDecoder = clientMessageDecoder;
    }

    public StateManager getStateManager() {
        return stateManager;
    }

    public void setStateManager(StateManager stateManager) {
        this.stateManager = stateManager;
    }

    public ModelManager getModelManager() {
        return modelManager;
    }

    public void setModelManager(ModelManager modelManager) {
        this.modelManager = modelManager;
    }

    public ListenerRegistry getListenerRegistry() {
        return listenerRegistry;
    }

    public void setListenerRegistry(ListenerRegistry listenerRegistry) {
        this.listenerRegistry = listenerRegistry;
    }

    public ClientMessageEncoder getClientMessageEncoder() {
        return clientMessageEncoder;
    }

    public void setClientMessageEncoder(ClientMessageEncoder clientMessageEncoder) {
        this.clientMessageEncoder = clientMessageEncoder;
    }

    public ELContext getELContext() {
        return elContext;
    }

    public void setELContext(ELContext elContext) {
        this.elContext = elContext;
    }
}
