package at.irian.ankor.api.application;

import at.irian.ankor.api.context.Context;

import java.io.Serializable;

/**
 */
public interface StateManager {
    Serializable restoreModel(Context ctx);
    void saveModel(Context ctx, Serializable model);
}
