package at.irian.ankor.viewmodel;

import at.irian.ankor.messaging.AnkorIgnore;
import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public abstract class ViewModelBase {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelBase.class);

    @AnkorIgnore
    private final Ref viewModelRef;

    protected ViewModelBase(Ref viewModelRef) {
        this.viewModelRef = viewModelRef;
        initialize(viewModelRef);
    }

    protected Ref thisRef() {
        return viewModelRef;
    }

    protected Ref thisRef(String property) {
        return viewModelRef.appendPath(property);
    }

    private void initialize(Ref viewModelRef) {
        for (ViewModelPostProcessor viewModelPostProcessor : viewModelRef.context().viewModelPostProcessors()) {
            viewModelPostProcessor.postProcess(this, viewModelRef);
        }
    }

}
