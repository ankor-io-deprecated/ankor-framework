package at.irian.ankor.sample.fx.view;

/**
 * @author Thomas Spiegl
 */
public class Tab<T> {

    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tab.class);
    private final String id;
    private T model;

    public Tab(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public T getModel() {
        return model;
    }

    public void setModel(T model) {
        this.model = model;
    }
}
