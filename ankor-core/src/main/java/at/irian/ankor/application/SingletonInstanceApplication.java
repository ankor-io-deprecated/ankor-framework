package at.irian.ankor.application;

import java.util.Map;

/**
 * An Application that does not support the creation of new instances.
 * Typically used in client applications that do not support external model connection requests.
 *
 * @author Manfred Geiler
 */
public class SingletonInstanceApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SingletonInstanceApplication.class);

    private final String name;

    public SingletonInstanceApplication(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ApplicationInstance getApplicationInstance(Map<String, Object> connectParameters) {
        return null;
    }

}
