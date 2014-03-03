package at.irian.ankor.application;

/**
 * @author Manfred Geiler
 */
public abstract class BaseApplication implements Application {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(BaseApplication.class);

    private final String name;

    public BaseApplication(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
