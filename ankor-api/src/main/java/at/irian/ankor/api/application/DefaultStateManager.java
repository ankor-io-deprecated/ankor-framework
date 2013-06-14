package at.irian.ankor.api.application;

import at.irian.ankor.api.context.ServerContext;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class DefaultStateManager implements StateManager {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultStateManager.class);

    private Object model;

    public Object getModel() {
        return model;
    }

    public void setModel(Object model) {
        this.model = model;
    }



    @Override
    public Object restoreModel(ServerContext ctx) {
        return model;
    }

    @Override
    public void saveModel(ServerContext ctx, Object model) {
        this.model = model;
    }
}
