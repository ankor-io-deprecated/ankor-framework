package at.irian.ankor.action;

/**
 * @author MGeiler (Manfred Geiler)
 */
public class SimpleAction implements Action {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(SimpleAction.class);

    private String name;

    protected SimpleAction() {}

    public SimpleAction(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "SimpleAction{" +
               "name='" + name + '\'' +
               '}';
    }
}
