package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.Set;

/**
 * @author Manfred Geiler
 */
public interface ApplicationInstance {

    void init(RefContext refContext);

    Set<String> getKnownRootNames();

    Object getModelRoot(String rootVarName);

    void setModelRoot(String rootVarName, Object bean);

    void release();

}
