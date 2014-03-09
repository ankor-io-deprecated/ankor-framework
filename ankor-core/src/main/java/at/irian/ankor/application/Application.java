package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.Map;

/**
 * @author Manfred Geiler
 */
public interface Application {

    /**
     * @return the name of this application
     */
    String getName();

    /**
     * Lookup a model by one or more (application specific) criteria.
     * Typical criteria are:
     * <ul>
     *     <li>username</li>
     *     <li>browser cookie</li>
     *     <li>device id</li>
     *     <li>...</li>
     * </ul>
     *
     * Applications are free to just return null on every call to this method.
     * Returning null is the expected behaviour for applications that do not
     * support restoring (user) sessions.
     *
     * @param modelName       name of model
     * @param connectCriteria lookup criterions
     * @return existing Model instance or null if there is no matching model yet
     */
    Object lookupModel(String modelName, Map<String, Object> connectCriteria);

    /**
     * @param modelName
     * @return
     */
    Object createModel(String modelName, RefContext refContext);

    void releaseModel(String modelName, Object model);

}
