package at.irian.ankor.application;

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
     * Create a new ApplicationInstance or return an existing instance, depending on the given connect parameters.
     * It is allowed to return a null instance. This will be interpreted as if the application does not want
     * to connect to a remote partner.
     *
     * Note: Must be thread-safe!
     *
     * @param connectParameters  application specific parameters or null if no parameters are needed (or supported)
     * @return an ApplicationInstance that is somehow associated to the given parameters
     */
    ApplicationInstance getApplicationInstance(Map<String,Object> connectParameters);

}
