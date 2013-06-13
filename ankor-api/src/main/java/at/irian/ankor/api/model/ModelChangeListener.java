package at.irian.ankor.api.model;

import at.irian.ankor.api.context.Context;

/**
 */
public interface ModelChangeListener {
    void handleModelChanged(Context context, ModelChange change);
}
