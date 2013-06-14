package at.irian.ankor.api.application;

import at.irian.ankor.api.context.ServerContext;

import java.io.Serializable;

/**
 */
public interface StateManager {
    Object restoreModel(ServerContext ctx);
    void saveModel(ServerContext ctx, Object model);
}
