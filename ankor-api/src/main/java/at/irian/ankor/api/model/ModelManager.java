package at.irian.ankor.api.model;

import java.util.List;

/**
 */
public interface ModelManager {

    void applyChange(Object model, ModelChange change);

    List<ModelChange> discoverChanges(Object oldModel, Object newModel);

}
