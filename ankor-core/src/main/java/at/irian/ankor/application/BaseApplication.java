package at.irian.ankor.application;

/**
 * Convenient base type for Ankor Applications.
 *
 * @author Manfred Geiler
 */
public abstract class BaseApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BaseApplication.class);

    private final String name;

    /**
     * @param name  name of this Ankor application
     */
    public BaseApplication(String name) {
        this.name = name;
    }

    /**
     *
     * @return the name of this Ankor application
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return always false (maybe overwritten by derived classes)
     */
    @Override
    public boolean isStateless() {
        return false;
    }
}
