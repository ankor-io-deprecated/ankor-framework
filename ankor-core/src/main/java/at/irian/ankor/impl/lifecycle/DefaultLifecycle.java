package at.irian.ankor.impl.lifecycle;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.action.ModelActionListener;
import at.irian.ankor.api.application.Application;
import at.irian.ankor.api.application.StateManager;
import at.irian.ankor.api.context.ServerContext;
import at.irian.ankor.api.event.ListenerRegistry;
import at.irian.ankor.api.lifecycle.Lifecycle;
import at.irian.ankor.api.lifecycle.PhaseId;
import at.irian.ankor.api.model.ModelChange;
import at.irian.ankor.api.model.ModelManager;
import at.irian.ankor.api.protocol.ClientMessageDecoder;
import at.irian.ankor.api.protocol.ClientMessageEncoder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 */
public class DefaultLifecycle implements Lifecycle {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultLifecycle.class);

    @Override
    public void execute(ServerContext context) {

        Application application = Application.getInstance();

        for (PhaseId phaseId : PhaseId.values()) {
            // todo:  pre phase listeners

            switch(phaseId) {
                case DecodeClientMessage:
                    executeDecodeClientMessage(application, context);
                    break;

                case RestoreModel:
                    executeRestoreModel(application, context);
                    break;

                case UpdateModelValues:
                    executeUpdateModelValues(application, context);
                    break;

                case InvokeApplication:
                    executeInvokeApplication(application, context);
                    break;

                case DetectModelChanges:
                    executeDetectModelChanges(application, context);
                    break;

                case SaveModel:
                    executeSaveModel(application, context);
                    break;

                case EncodeResponseMessage:
                    executeEncodeResponseMessage(application, context);
                    break;
            }

            // todo: post phase listeners
        }
    }

    private void executeDecodeClientMessage(Application application, ServerContext context) {
        ClientMessageDecoder decoder = application.getClientMessageDecoder();
        Object msg = context.getMessageFromClient();

        List<ModelChange> modelChanges = decoder.decodeModelChanges(msg);
        context.setModelChanges(modelChanges);

        List<ModelAction> modelActions = decoder.decodeModelActions(msg);
        context.setModelActions(modelActions);
    }

    private void executeRestoreModel(Application application, ServerContext context) {
        StateManager stateManager = application.getStateManager();
        Serializable model = stateManager.restoreModel(context);
        context.setModel(model);
    }

    private void executeUpdateModelValues(Application application, ServerContext context) {
        ModelManager modelManager = application.getModelManager();
        Object model = context.getModel();
        List<ModelChange> modelChanges = context.getModelChanges();
        for (ModelChange modelChange : modelChanges) {
            modelManager.applyChange(model, modelChange);  //todo: do this with a DefaultModelChangeListener?
            //todo: call ModelChangeListeners
        }
        context.setModelChanges(Collections.<ModelChange>emptyList());
    }

    private void executeInvokeApplication(Application application, ServerContext context) {
        ListenerRegistry listenerRegistry = application.getListenerRegistry();
        List<ModelAction> modelActions = context.getModelActions();
        for (ModelAction action : modelActions) {
            Collection<ModelActionListener> listeners = listenerRegistry.getActionListeners();
            for (ModelActionListener listener : listeners) {
                listener.handleAction(context, action);
            }
        }
    }

    private void executeDetectModelChanges(Application application, ServerContext context) {
        ModelManager modelManager = application.getModelManager();
        // modelManager.discoverChanges()
        List<ModelChange> modelChanges = Collections.emptyList(); //todo
        // todo
        context.setModelChanges(modelChanges);
    }

    private void executeSaveModel(Application application, ServerContext context) {
        StateManager stateManager = application.getStateManager();
        Serializable model = context.getModel();
        stateManager.saveModel(context, model);
    }

    private void executeEncodeResponseMessage(Application application, ServerContext context) {
        ClientMessageEncoder encoder = application.getClientMessageEncoder();
        List<ModelChange> modelChanges = context.getModelChanges();
        Object serverToClientMsg = encoder.encodeClientMessage(modelChanges);
        context.setMessageToClient(serverToClientMsg);
    }
}
