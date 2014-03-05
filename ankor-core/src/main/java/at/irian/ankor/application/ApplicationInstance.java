package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.Set;

/**
 * @author Manfred Geiler
 */
public interface ApplicationInstance {

    /**
     * Initialize this ApplicationInstance.
     * Called when a new ModelSession was created and bound to this ApplicationInstance.
     * @param refContext the RefContext that is related to the newly created ModelSession
     */
    void init(RefContext refContext);

    Set<String> getKnownRootNames();

    Object getModelRoot(String modelName);

    void setModelRoot(String modelName, Object bean);

    void release();

}
