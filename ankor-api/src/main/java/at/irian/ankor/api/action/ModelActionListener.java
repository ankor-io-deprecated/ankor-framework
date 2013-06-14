package at.irian.ankor.api.action;

import at.irian.ankor.api.context.ServerContext;

/**
 */
public interface ModelActionListener {
    void handleAction(ServerContext context, ModelAction action);
}
