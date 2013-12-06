package at.irian.ankor.viewmodel;

import at.irian.ankor.ref.Ref;
import at.irian.ankor.viewmodel.metadata.BeanMetadata;

/**
 * @author Manfred Geiler
 */
public final class ViewModelSupport {
    //private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(ViewModelSupport.class);

    private ViewModelSupport() {}

    public static void invokePostProcessorsOn(Object viewModelObject, Ref viewModelRef, BeanMetadata metadata) {
        for (ViewModelPostProcessor viewModelPostProcessor : viewModelRef.context().viewModelPostProcessors()) {
            viewModelPostProcessor.postProcess(viewModelObject, viewModelRef, metadata);
        }
    }
}
