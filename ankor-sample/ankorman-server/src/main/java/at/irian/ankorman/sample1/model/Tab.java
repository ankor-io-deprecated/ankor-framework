package at.irian.ankorman.sample1.model;

import at.irian.ankor.annotation.ChangeListener;
import at.irian.ankor.model.ViewModelBase;
import at.irian.ankor.model.ViewModelProperty;
import at.irian.ankor.ref.Ref;

/**
 * @author Thomas Spiegl
 */
public class Tab<T> extends ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tab.class);

    private String id;

    private T model;

    private ViewModelProperty<String> name;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    Tab() {
        super(null);
    }

    public Tab(String id, Ref tabRef, String initialTabName) {
        super(tabRef);
        this.id = id;
        this.name.set(initialTabName);
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

    public ViewModelProperty<String> getName() {
        return name;
    }

    public void setName(ViewModelProperty<String> name) {
        this.name = name;
    }

    public void close() {
        thisRef().setValue(null);
    }
}
