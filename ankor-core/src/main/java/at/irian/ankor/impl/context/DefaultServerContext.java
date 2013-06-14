package at.irian.ankor.impl.context;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.context.ServerContext;
import at.irian.ankor.api.model.ModelChange;

import javax.el.ELContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultServerContext implements ServerContext {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultServerContext.class);

    private Object messageFromClient;
    private Object messageToClient;

    public void setMessageFromClient(Object messageFromClient) {
        this.messageFromClient = messageFromClient;
    }

    @Override
    public Object getMessageFromClient() {
        return messageFromClient;
    }

    @Override
    public void setMessageToClient(Object messageToClient) {
        this.messageToClient = messageToClient;
    }

    public Object getMessageToClient() {
        return messageToClient;
    }


    @Override
    public void setModelId(Serializable modelId) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Serializable getModelId() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setModel(Serializable model) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Serializable getModel() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setModelChanges(List<ModelChange> modelChanges) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ModelChange> getModelChanges() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setModelActions(List<ModelAction> modelChanges) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<ModelAction> getModelActions() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ELContext getELContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setELContext(ELContext elContext) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
