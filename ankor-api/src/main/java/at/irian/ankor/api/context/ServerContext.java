package at.irian.ankor.api.context;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;

import javax.el.ELContext;
import java.io.Serializable;
import java.util.List;

/**
 */
public interface ServerContext {

    Object getMessageFromClient();
    void setMessageToClient(Object message);

    void setModelChanges(List<ModelChange> modelChanges);
    List<ModelChange> getModelChanges();

    void setModelActions(List<ModelAction> modelChanges);
    List<ModelAction> getModelActions();

    void setModel(Serializable model);
    Serializable getModel();

}
