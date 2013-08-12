package at.irian.ankor.sample1.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.ViewModelBase;
import at.irian.ankor.viewmodel.ViewModelProperty;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class Tab<T> extends ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tab.class);

    private String id;

    private String type;

    private T model;

    private ViewModelProperty<String> name;

    public Tab(String id, Ref tabRef, String initialTabName, String type) {
        super(tabRef);
        this.id = id;
        this.name.set(initialTabName);
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void close() {
        thisRef().setValue(null);
    }

}
