package at.irian.ankor.model;

import at.irian.ankor.ref.Ref;

/**
 * @author Manfred Geiler
 */
public interface ViewModelPostProcessor {
    void postProcess(ViewModelBase viewModelObject, Ref viewModelRef);
}
