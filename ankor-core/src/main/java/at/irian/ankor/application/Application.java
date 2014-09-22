package at.irian.ankor.application;

import at.irian.ankor.ref.RefContext;

import java.util.Map;
import java.util.Set;

/**
 * By means of an {@link Application} the developer tells the Ankor framework the main infos about
 * his Ankor application, like:
 * <ul>
 *     <li>name of the application (for logging)</li>
 *     <li>model lookup behaviour</li>
 *     <li>init/cleanup behaviour</li>
 *     <li>stateless/stateful</li>
 * </ul>
 *
 * @author Manfred Geiler
 */
public interface Application {

    /**
     * @return the name of this application
     */
    String getName();

    /**
     * @return true, if Ankor shall drive in stateless mode
     */
    boolean isStateless();

    /**
     * @return a list of model names this application supports
     */
    Set<String> getKnownModelNames();

    /**
     * Lookup a model by one or more (application specific) connect parameters.
     * Typical parameters are:
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
     * @param modelName         name of model
     * @param connectParameters lookup criteria
     * @return existing Model instance or null if there is no matching model yet
     */
    Object lookupModel(String modelName, Map<String, Object> connectParameters);

    /**
     * Create a new instance of a model root that matches the given model name and the (application specific) connect
     * parameters.
     *
     * @param modelName         name of model
     * @param connectParameters (optional) connect parameters
     * @param refContext        RefContext for the corresponding ModelSession
     * @return newly created model root
     */
    Object createModel(String modelName, Map<String, Object> connectParameters, RefContext refContext);

    /**
     * Free all resources that might have been allocated for the given model instance.
     * @param modelName    name of model
     * @param modelRoot    root of model instance to free
     */
    void releaseModel(String modelName, Object modelRoot);

    /**
     * Free all resources associated by this Ankor application.
     */
    void shutdown();

}
