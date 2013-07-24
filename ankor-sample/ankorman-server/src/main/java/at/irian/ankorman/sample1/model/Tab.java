package at.irian.ankorman.sample1.model;

import at.irian.ankor.model.ModelProperty;
import at.irian.ankor.ref.Ref;

/**
 * @author Thomas Spiegl
 */
public class Tab<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Tab.class);

    private String id;

    private ModelProperty<String> name;

    private T model;

    /**
     * for deserialization only
     */
    @SuppressWarnings("UnusedDeclaration")
    Tab() {}

    public Tab(String id, Ref tabRef, String initialTabName) {
        this.id = id;
        this.name = ModelProperty.createReferencedProperty(tabRef.append("name"), initialTabName);
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

    public ModelProperty<String> getName() {
        return name;
    }

    public void setName(ModelProperty<String> name) {
        this.name = name;
    }
}
