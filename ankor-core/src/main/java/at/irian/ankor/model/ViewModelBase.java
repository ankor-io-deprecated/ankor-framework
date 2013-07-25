package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author Manfred Geiler
 */
public abstract class ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelBase.class);

    @JsonIgnore
    private final Ref viewModelRef;

    protected ViewModelBase(Ref viewModelRef) {
        this.viewModelRef = viewModelRef;
        initialize();
    }

    protected Ref thisRef() {
        return viewModelRef;
    }

    private void initialize() {
        new ViewModelInitializer(this, thisRef()).initAll();
    }

}
