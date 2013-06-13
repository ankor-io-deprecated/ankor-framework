package at.irian.ankor.api.action;

import at.irian.ankor.api.context.Context;

/**
 */
public interface ModelActionListener {
    void handleAction(Context context, ModelAction action);
}
