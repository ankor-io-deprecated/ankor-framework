package at.irian.ankor.impl.msgbus;

import at.irian.ankor.api.action.ModelAction;
import at.irian.ankor.api.model.ModelChange;

import java.util.List;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class MockClientMessage {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MockClientMessage.class);

    private final List<ModelChange> modelChanges;
    private final List<ModelAction> modelActions;

    public MockClientMessage(List<ModelChange> modelChanges,
                             List<ModelAction> modelActions) {
        this.modelChanges = modelChanges;
        this.modelActions = modelActions;
    }

    public List<ModelChange> getModelChanges() {
        return modelChanges;
    }

    public List<ModelAction> getModelActions() {
        return modelActions;
    }
}
