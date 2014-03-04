package at.irian.ankorsamples.animals.viewmodel;

import at.irian.ankor.pattern.AnkorPatterns;
import at.irian.ankor.ref.Ref;

/**
 * @author Thomas Spiegl
 */
@SuppressWarnings("UnusedDeclaration")
public class Panel<T> {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(Panel.class);

    private final String id;

    private final String type;

    private final T model;

    private String name;

    public Panel(String id, Ref myRef, String initialPanelName, String type, T model) {
        this.id = id;
        this.name = initialPanelName;
        this.type = type;
        this.model = model;
        AnkorPatterns.initViewModel(this, myRef);
    }

    public String getId() {
        return id;
    }

    public T getModel() {
        return model;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

}
