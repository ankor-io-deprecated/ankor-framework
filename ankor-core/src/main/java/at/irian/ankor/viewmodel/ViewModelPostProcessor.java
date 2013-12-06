package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

/**
 * @author Manfred Geiler
 */
public interface ViewModelPostProcessor {
    void postProcess(Object viewModelObject, Ref viewModelRef, BeanMetadata metadata);
}
