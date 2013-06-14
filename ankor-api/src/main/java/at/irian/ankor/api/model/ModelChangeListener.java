package at.irian.ankor.api.model;

import at.irian.ankor.api.context.ServerContext;

/**
 */
public interface ModelChangeListener {
    void handleModelChanged(ServerContext context, ModelChange change);
}
