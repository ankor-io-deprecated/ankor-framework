package at.irian.ankor.action;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAction implements Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAction.class);

    private final String name;

    protected SimpleAction(String name) {
        this.name = name;
    }

    public static SimpleAction create(String name) {
        return new SimpleAction(name);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "SimpleAction{" +
               "name='" + name + '\'' +
               '}';
    }
}
